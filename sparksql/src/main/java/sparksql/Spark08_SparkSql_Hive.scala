package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark08_SparkSql_Hive {

  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.SparkSession
    val conf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("SparkSQL")
    //创建 SparkSession 对象
    val spark: SparkSession =
      SparkSession.builder().enableHiveSupport().config(conf).getOrCreate()
    spark.sql("show databases").show()

    //释放资源
    spark.stop()
  }

}
