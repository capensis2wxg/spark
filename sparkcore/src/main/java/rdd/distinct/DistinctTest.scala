package rdd.distinct

/*
    @author wxg
    @date 2021/7/31-20:46
 */
object DistinctTest {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5, 1, 2, 3))
    val rdd1: RDD[Int] = rdd.distinct()
    rdd1.collect().foreach(println)
  }
}
