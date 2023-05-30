package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark08_SparkSql_Hive {

	def main(args: Array[String]): Unit = {
		System.setProperty("HADOOP_USER_NAME", "wxg")
		import org.apache.spark.SparkConf
		import org.apache.spark.sql.SparkSession

		val conf: SparkConf = new SparkConf()
				.setMaster("local[*]")
				.setAppName("SparkSQL")
		//创建 SparkSession 对象
		val spark: SparkSession = SparkSession.builder().enableHiveSupport().config(conf).getOrCreate()
		spark.sql("""show databases""".stripMargin).show()
//		spark.sql("""use default""")
//		spark.sql("""show tables""").show()
//		spark.sql("""select * from default.student""").show()
		spark.sql("""use spark_hive""")
		spark.sql("""select count(*) from city_info""").show()
		//释放资源
		spark.stop()
	}

}
