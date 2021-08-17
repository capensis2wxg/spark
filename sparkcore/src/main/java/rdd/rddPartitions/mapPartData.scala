package rdd.rddPartitions

/*
    @author wxg
    @date 2021/7/23-8:48
 */
object mapPartData {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    val mapRdd: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((index, iter) => {
      iter.map(num => { (index, num) })
    })
    mapRdd.collect().foreach(println)
    sc.stop()
  }

}
