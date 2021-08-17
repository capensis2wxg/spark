package rdd.filter

/*
    @author wxg
    @date 2021/7/23-12:27
 */
/**
  * ➢  函数签名
		def rdd.filter(f: T => Boolean): RDD[T]
   ➢  函数说明
	将数据根据指定的规则进行筛选过滤，符合规则的数据保留，不符合规则的数据丢弃。
	当数据进行筛选过滤后，分区不变，但是分区内的数据可能不均衡，生产环境下，可能会出现数据倾斜。
  */
object filter {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val dataRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val dataRDD1: RDD[Int] = dataRDD.filter(_ % 2 == 0)
    dataRDD1.collect().foreach(println)
    sc.stop()
  }
}
