package sparksql

/*
    @author wxg
    @date 2021/8/15-1:07
 */
object Spark01_SparkSql_Basic {

  import org.apache.spark.SparkConf

  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
    //	TODO 创建SparkSql的运行环境
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sparkSql: SparkSession =
      SparkSession.builder().config(sparkConf).getOrCreate()
    import org.apache.spark.rdd.RDD
    import org.apache.spark.sql.Row
    import sparkSql.implicits._

    //	TODO 执行逻辑操作

    //  DataFrame
//    val dataFrame: DataFrame = sparkSql.read
//      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
//      .json("sparksql/data/user.json")
//    dataFrame.show()
//    //  DataFrame ===> SQL
//    dataFrame.createOrReplaceTempView("user")
//    sparkSql.sql("select * from user").show()
//    sparkSql.sql("select username, age from user").show()
//    sparkSql.sql("select avg(age) from user").show()
    //  DataFrame ===> DSL
//    dataFrame.select("username", "age").show()
//    dataFrame.select('age + 1).show()
//    dataFrame.select($"age" + 1).show()

    //  DataSet  DataFrame其实是特定泛型的DataSet
//    val seq = Seq(1, 2, 3, 4)
//    val dataSet: Dataset[Int] = seq.toDS()
//    dataSet.show()
//      RDD  ===>  DataFrame
    val rdd: RDD[(Int, String, Int)] = sparkSql.sparkContext.makeRDD(
      List(
        (1, "zhangsan", 20),
        (2, "list", 30)
      )
    )

    val dataFrame1: DataFrame = rdd.toDF("id", "name", "age")
    val rowRDD: RDD[Row] = dataFrame1.rdd

    //  DataFrame <=> DataSet
    val dataSet1: Dataset[User] = dataFrame1.as[User]
    val dataFrame2: DataFrame = dataSet1.toDF()
    dataFrame2.show()

    // RDD <=> DataSet
    val rddSet: Dataset[User] = rdd
      .map { case (id, name, age) => User(id, name, age) }
      .toDS()
    val userRDD: RDD[User] = rddSet.rdd

    //	TODO 关闭环境
    sparkSql.close()

  }

  case class User(id: Long, name: String, age: Long)

}
