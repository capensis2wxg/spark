package rdd.rddPartitions

/*
    @author wxg
    @date 2021/7/23-8:30
 */
object mapPartMax {
  import org.apache.spark.{SparkConf, SparkContext}
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val mapRdd: RDD[Int] = rdd.mapPartitions(iter => {
      List(iter.max).iterator
    })
    mapRdd.collect().foreach(println)
    sc.stop()
  }
}
