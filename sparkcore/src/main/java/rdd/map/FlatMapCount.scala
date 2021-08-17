package rdd.map

/*
    @author wxg
    @date 2021/7/23-9:17
 */
object FlatMapCount {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[String] = sc.makeRDD(List("hello spark", "hello Scala"))
    val flapmapRdd: RDD[String] = rdd.flatMap(s => {
      s.split(" ")
    })
    flapmapRdd.collect().foreach(println)
    sc.stop()
  }

}
