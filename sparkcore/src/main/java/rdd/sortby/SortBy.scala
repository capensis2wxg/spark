package rdd.sortby

import org.apache.spark.{SparkConf, SparkContext}

/*
    @author wxg
    @date 2021/7/31-23:21
 */
object SortBy {

  /*
	➢  函数签名
	 def sortBy[K](f: (T) => K,  ascending: Boolean = true, numPartitions: Int = this.partitions.length) (implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T]
	 ➢  函数说明
	   该操作用于排序数据。在排序之前，可以将数据通过 f 函数进行处理，之后按照 f 函数处理的结果进行排序，默认为升序排列。
	   排序后新产生的 RDD 的分区数与原 RDD 的分区数一致。中间存在 shuffle 的过程
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val dataRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 1, 2), 2)
    val dataRDD1: RDD[Int] = dataRDD.sortBy((num: Int) => num, false, 4)
    dataRDD1.saveAsTextFile("SortBy")
    dataRDD1.collect().foreach(println)

    val rdd2: RDD[(String, Int)] =
      sc.makeRDD(List(("1", 1), ("11", 2), ("2", 3)), 2)
    val newRDD: RDD[(String, Int)] = rdd2.sortBy(_._1.toInt)
    newRDD.collect().foreach(println)

    sc.stop()
  }
}
