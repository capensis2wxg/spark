package rdd.rddPartitions

/*
    @author wxg
    @date 2021/7/23-8:45
 */
object mapPartIndex {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val mapRdd: RDD[Int] = rdd.mapPartitionsWithIndex((index, iter) => {
      if (index == 1) iter else Nil.iterator
    })
    mapRdd.collect().foreach(println)
    sc.stop()
  }

}
