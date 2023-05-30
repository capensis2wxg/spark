package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark07_SparkSql_JDBC1 {
  case class User2(age: Long)
  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.{Dataset, SparkSession}
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("SparkSQL")
    //创建 SparkSession 对象
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    import org.apache.spark.rdd.RDD
    import org.apache.spark.sql.SaveMode
    import spark.implicits._
    import java.util.Properties
    val rdd: RDD[User2] = spark.sparkContext.makeRDD(List(User2(20), User2(30)))
    val ds: Dataset[User2] = rdd.toDS
    //方式 1：通用的方式  format 指定写出类型
    ds.write
      .format("jdbc")
      .option("url", "jdbc:mysql://hadoop102:3306/test")
      .option("user", "root")
      .option("password", "wxg124328l")
      .option("dbtable", "students")
      .mode(SaveMode.Append)
      .save()

    //方式 2：通过 jdbc 方法
    val props: Properties = new Properties()
    props.setProperty("user", "root")
    props.setProperty("password", "wxg124328l")
    ds.write
      .mode(SaveMode.Append)
      .jdbc("jdbc:mysql://hadoop102:3306/test", "students", props)

    //释放资源
    spark.stop()
  }

}
