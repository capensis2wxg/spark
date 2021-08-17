package sparksql

/*
    @author wxg
    @date 2021/8/15-15:09
 */
object Spark02_SparkSql_Udf {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
    //	TODO 创建SparkSql的运行环境
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sparkSql: SparkSession =
      SparkSession.builder().config(sparkConf).getOrCreate()
    //	读取数据
    val dataFrame: DataFrame = sparkSql.read
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .json("sparksql/data/user.json")
    dataFrame.createOrReplaceTempView("user")
    //	定义函数
    sparkSql.udf.register("prefixName", (name: String) => "name " + name)
    //	调用函数
    sparkSql.sql("select age, prefixName(username) from user").show()

  }

}
