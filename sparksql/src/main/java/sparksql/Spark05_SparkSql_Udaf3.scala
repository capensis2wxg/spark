package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark05_SparkSql_Udaf3 {

  import org.apache.spark.sql.expressions.Aggregator

  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.{DataFrame, Dataset, SparkSession, TypedColumn}
    //	TODO 创建SparkSql的运行环境
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sparkSql: SparkSession =
      SparkSession.builder().config(sparkConf).getOrCreate()
    //	读取数据
    val dataFrame: DataFrame = sparkSql.read
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("sparksql/data/user.json")
    /*
      早期版本中，spark不能在sql中使用强类型UDAF操作
      //SQL or DSL
      早期的UDAF强类型聚合函数使用DSL语法操作
     */
    import sparkSql.implicits._
    val ds: Dataset[User] = dataFrame.as[User]
    //  将UDAF函数转换为查询的列对象
    val udafCol: TypedColumn[User, Long] = new AvgUdf().toColumn
    ds.select(udafCol).show
  }

  /**
    * 自定义聚合函数类：计算年龄的平均值
    * 1、继承org.apache.spark.sql.expressions.Aggregator#Aggregator()
    * 2、重写方法
    */
  case class User(username: String, age: Long)
  case class Buffer(var total: Long, var count: Long)
  class AvgUdf extends Aggregator[User, Buffer, Long] {
    import org.apache.spark.sql.{Encoder, Encoders}
    override def zero: Buffer = { Buffer(0L, 0L) }
    //	根据输入的数据更新缓冲区的数据
    override def reduce(buffer: Buffer, in: User): Buffer = {
      buffer.total = buffer.total + in.age
      buffer.count = buffer.count + 1
      buffer
    }
    //  合并缓冲区
    override def merge(b1: Buffer, b2: Buffer): Buffer = {
      b1.total = b1.total + b2.total
      b1.count = b1.count + b2.count
      b1
    }
    //  计算结果
    override def finish(buffer: Buffer): Long = buffer.total / buffer.count
    //  编码区的缓冲操作
    override def bufferEncoder: Encoder[Buffer] = Encoders.product
    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }

}
