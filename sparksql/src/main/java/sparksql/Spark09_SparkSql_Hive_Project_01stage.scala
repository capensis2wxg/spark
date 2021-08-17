package sparksql

/*
    @author wxg
    @date 2021/8/15-15:59
 */
object Spark09_SparkSql_Hive_Project_01stage {

  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkConf
    import org.apache.spark.sql.SparkSession
    System.setProperty("HADOOP_USER_NAME", "wxg")
    val conf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("SparkSQL")
    //创建 SparkSession 对象
    val spark: SparkSession =
      SparkSession.builder().enableHiveSupport().config(conf).getOrCreate()
    //  连接数据库
    spark.sql("use spark_hive")
    //  准备数据
    spark.sql(
      """
            |CREATE TABLE if not exists`user_visit_action`( 
            |  `date` string,  
            |  `user_id` bigint, 
            |  `session_id` string, 
            |  `page_id` bigint, 
            |  `action_time` string, 
            |  `search_keyword` string, 
            |  `click_category_id` bigint, 
            |  `click_product_id` bigint, 
            |  `order_category_ids` string, 
            |  `order_product_ids` string, 
            |  `pay_category_ids` string, 
            |  `pay_product_ids` string, 
            |  `city_id` bigint) 
            |row format delimited fields terminated by '\t'
      """.stripMargin
    )
    spark.sql(
      """
		  |load data local inpath 'sparksql/data/user_visit_action.txt' into table spark_hive.user_visit_action
      """.stripMargin
    )

    spark.sql(
      """
          |CREATE TABLE if not exists`product_info`( 
          |  `product_id` bigint, 
          |  `product_name` string, 
          |  `extend_info` string) 
          |row format delimited fields terminated by '\t'
      """.stripMargin
    )
    spark.sql(
      """
          |load data local inpath 'sparksql/data/product_info.txt' into table spark_hive.product_info
      """.stripMargin
    )

    spark.sql(
      """
          |CREATE TABLE if not exists`city_info`( 
          |  `city_id` bigint, 
          |  `city_name` string, 
          |  `area` string) 
          |row format delimited fields terminated by '\t'
      """.stripMargin
    )
    spark.sql(
      """
          |load data local inpath 'sparksql/data/city_info.txt' into table spark_hive.city_info
      """.stripMargin
    )
    //释放资源
    spark.stop()
  }

}
