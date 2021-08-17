package rdd.filter

/*
    @author wxg
    @date 2021/7/31-20:29
 */
object Exer {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[String] = sc.textFile("sparkcore/data/apache.log")
    rdd
      .filter(line => {
        val datas: Array[String] = line.split(" ")
        val time: String = datas(3)
        time.startsWith("17/05/2015")
      })
      .collect()
      .foreach(println)
    sc.stop()
  }
}
