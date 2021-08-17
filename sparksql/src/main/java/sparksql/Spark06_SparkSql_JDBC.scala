package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark06_SparkSql_JDBC {

  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.{DataFrame, SparkSession}
    //	TODO 创建SparkSql的运行环境
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sparkSql: SparkSession =
      SparkSession.builder().config(sparkConf).getOrCreate()
    import org.apache.spark.sql.SaveMode
    import java.util.Properties

    //  读取数据
    //方式 1：通用的 load 方法读取
    sparkSql.read
      .format("jdbc")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("url", "jdbc:mysql://hadoop102:3306/shucang")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "wxg124328l")
      .option("dbtable", "user_info")
      .load()
      .show

    //方式 2:通用的 load 方法读取 参数另一种形式
    sparkSql.read
      .format("jdbc")
      .options(
        Map(
          "timestampFormat" -> "yyyy/MM/dd HH:mm:ss ZZ",
          "url" -> "jdbc:mysql://hadoop102:3306/shucang?user=root&password=wxg124328l",
          "dbtable" -> "user_info",
          "driver" -> "com.mysql.jdbc.Driver"
        )
      )
      .load()
      .show

    //方式 3:使用 jdbc 方法读取
    val props1: Properties = new Properties()
    props1.setProperty("user", "root")
    props1.setProperty("password", "wxg124328l")
    val df: DataFrame =
      sparkSql.read.jdbc(
        "jdbc:mysql://hadoop102:3306/shucang",
        "user_info",
        props1
      )
    df.show

    //方式 1：通用的方式  format 指定写出类型
    df.write
      .format("jdbc")
      .option("url", "jdbc:mysql://hadoop102:3306/shucang")
      .option("user", "root")
      .option("password", "wxg124328l")
      .option("dbtable", "user_info")
      //      .mode(SaveMode.Append)
      .mode(SaveMode.Overwrite)
      .save()

    //方式 2：通过 jdbc 方法
    val props2: Properties = new Properties()
    props2.setProperty("user", "root")
    props2.setProperty("password", "wxg124328l")
    df.write
    //      .mode(SaveMode.Append)
      .mode(SaveMode.Overwrite)
      .jdbc("jdbc:mysql://hadoop102:3306/shucang", "user_info", props2)

    // TODO 关闭环境
    sparkSql.close()

  }

}
