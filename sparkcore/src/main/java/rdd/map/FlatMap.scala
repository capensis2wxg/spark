package rdd.map

/*
    @author wxg
    @date 2021/7/23-9:24
 */
object FlatMap {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[List[Int]] = sc.makeRDD(List(List(1, 2), List(3, 4)))
    val flatmap: RDD[Int] = rdd.flatMap(list => list)
    flatmap.collect().foreach(println)
    sc.stop()
  }

}
