package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark03_SparkSql_Udaf1 {

  import org.apache.spark.sql.expressions.UserDefinedAggregateFunction

  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.{DataFrame, SparkSession}
    //	TODO 创建SparkSql的运行环境
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sparkSql: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    //	读取数据
    val dataFrame: DataFrame = sparkSql.read.json("sparksql/data/user.json")
    dataFrame.createOrReplaceTempView("user")
    //	定义函数
    //  sparkSql.udf.register("prefixName", (name: String) => "name " + name)
    sparkSql.udf.register("ageAvg", new AvgUdf())
    //	调用函数
    sparkSql.sql("select ageAvg(age) from user").show()
  }

  /**
    * 自定义聚合函数类：计算年龄的平均值
    *<p>
    *  1、继承UserDefinedAggregateFunction
    *<p>
    *  2、重写方法
    */
  class AvgUdf extends UserDefinedAggregateFunction {
    import org.apache.spark.sql.Row
    import org.apache.spark.sql.expressions.MutableAggregationBuffer
    import org.apache.spark.sql.types.{DataType, LongType, StructType}
    //	输入数据的结构
    override def inputSchema: StructType = {
      import org.apache.spark.sql.types.{LongType, StructField}
      StructType(Array(StructField("age", LongType)))
    }
    //	缓冲区数据的结构：Buffer
    override def bufferSchema: StructType = {
      import org.apache.spark.sql.types.{LongType, StructField}
      StructType(
        Array(StructField("total", LongType), StructField("count", LongType))
      )
    }
    //	函数计算结果的数据类型
    override def dataType: DataType = LongType
    //	函数的稳定性
    override def deterministic: Boolean = true
    //	缓冲区的初始化
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      //buffer(0) = 0L
      //buffer(1) = 0L
      buffer.update(0, 0L)
      buffer.update(1, 0L)

    }
    //	根据输入的值更新缓冲区数据
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      buffer.update(0, buffer.getLong(0) + input.getLong(0))
      buffer.update(1, buffer.getLong(1) + 1)
    }
    //	缓冲区数据合并
    override def merge(
        buffer1: MutableAggregationBuffer,
        buffer2: Row
    ): Unit = {
      buffer1.update(0, buffer1.getLong(0) + buffer2.getLong(0))
      buffer1.update(1, buffer1.getLong(1) + buffer2.getLong(1))

    }
    //	计算结果
    override def evaluate(buffer: Row): Any = {
      buffer.getLong(0) / buffer.getLong(1)
    }
  }

}
