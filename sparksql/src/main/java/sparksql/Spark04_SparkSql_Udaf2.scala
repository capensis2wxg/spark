package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark04_SparkSql_Udaf2 {

  import org.apache.spark.sql.expressions.UserDefinedAggregateFunction

  def main(args: Array[String]): Unit = {
    import org.apache.spark.sql.{DataFrame, SparkSession}
    //	TODO 创建SparkSql的运行环境
    //using of udaf
    val sparkSql: SparkSession = SparkSession
      .builder()
      .master("local[*]")
      .appName("sparkSession")
      .getOrCreate()
    //	读取数据
    val dataFrame: DataFrame = sparkSql.read
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("sparksql/data/user.json")
    dataFrame.createOrReplaceTempView("user")
    //	定义函数
    //    sparkSql.udf.register("prefixName", (name: String) => "name " + name)
    sparkSql.udf.register("ageAvg", new AvgUdf)
    //	调用函数
    sparkSql.sql("select ageAvg(age) from user").show()
  }

  /**
    * 自定义聚合函数类：计算年龄的平均值
    * 1、继承org.apache.spark.sql.expressions.UserDefinedAggregateFunction#UserDefinedAggregateFunction()
    * 2、重写方法
    */
  case class Buffer(var total: Long, var count: Long)
  class AvgUdf extends UserDefinedAggregateFunction {
    import org.apache.spark.sql.Row
    import org.apache.spark.sql.expressions.MutableAggregationBuffer
    import org.apache.spark.sql.types._

    /**
      * A `StructType` represents data types of input arguments of this aggregate function.
      * For example, if a [[UserDefinedAggregateFunction]] expects two input arguments
      * with type of `DoubleType` and `LongType`, the returned `StructType` will look like
      *
      * ```
      *   new StructType()
      *    .add("doubleInput", DoubleType)
      *    .add("longInput", LongType)
      * ```
      *
      * The name of a field of this `StructType` is only used to identify the corresponding
      * input argument. Users can choose names to identify the input arguments.
      *
      * @since 1.5.0
      */
    override def inputSchema: StructType =
      StructType(StructField("input", IntegerType) :: Nil)

    //buffer datatype in compute
    /**
      * A `StructType` represents data types of values in the aggregation buffer.
      * For example, if a [[UserDefinedAggregateFunction]]'s buffer has two values
      * (i.e. two intermediate values) with type of `DoubleType` and `LongType`,
      * the returned `StructType` will look like
      *
      * ```
      *   new StructType()
      *    .add("doubleInput", DoubleType)
      *    .add("longInput", LongType)
      * ```
      *
      * The name of a field of this `StructType` is only used to identify the corresponding
      * buffer value. Users can choose names to identify the input arguments.
      *
      * @since 1.5.0
      */
    override def bufferSchema: StructType =
      StructType(
        StructField("sum", IntegerType) :: StructField(
          "count",
          IntegerType
        ) :: Nil
      )

    //output data type
    /**
      * The `DataType` of the returned value of this [[UserDefinedAggregateFunction]].
      *
      * @since 1.5.0
      */
    override def dataType: DataType = DoubleType

    //stable feature
    /**
      * Returns true iff this function is deterministic, i.e. given the same input,
      * always return the same output.
      *
      * @since 1.5.0
      */
    override def deterministic: Boolean = true

    //buffer data initial a value
    /**
      * Initializes the given aggregation buffer, i.e. the zero value of the aggregation buffer.
      *
      * The contract should be that applying the merge function on two initial buffers should just
      * return the initial buffer itself, i.e.
      * `merge(initialBuffer, initialBuffer)` should equal `initialBuffer`.
      *
      * @since 1.5.0
      */
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      buffer(0) = 0
      buffer(1) = 0
    }
    //compute in inner partition
    /**
      * Updates the given aggregation buffer `buffer` with new input data from `input`.
      *
      * This is called once per input row.
      *
      * @since 1.5.0
      */
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      //judge data is null
      if (!input.isNullAt(0)) {
        //sum of input data
        buffer(0) = buffer.getInt(0) + input.getInt(0)
        //sum of input data count
        buffer(1) = buffer.getInt(1) + 1
      }
    }
    //compute between partitions
    /**
      * Merges two aggregation buffers and stores the updated buffer values back to `buffer1`.
      *
      * This is called when we merge two partially aggregated data together.
      *
      * @since 1.5.0
      */
    override def merge(
        buffer1: MutableAggregationBuffer,
        buffer2: Row
    ): Unit = {
      //
      buffer1(0) = buffer1.getInt(0) + buffer2.getInt(0)
      buffer1(1) = buffer1.getInt(1) + buffer2.getInt(1)
    }

    //last step computing
    /**
      * Calculates the final result of this [[UserDefinedAggregateFunction]] based on the given
      * aggregation buffer.
      *
      * @since 1.5.0
      */
    override def evaluate(buffer: Row): Any = {
      buffer.getInt(0) / buffer.getInt(1).toDouble
    }
  }

}
