package rdd.distinct

/*
    @author wxg
    @date 2021/7/31-21:43
 */
object Zip {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
 ➢  函数签名
	def zip[U: ClassTag](other: RDD[U]): RDD[(T, U)]
 ➢  函数说明
	将两个 RDD 中的元素，以键值对的形式进行合并。其中，键值对中的 Key 为第 1 个 RDD 中的元素，Value 为第 2 个 RDD 中的相同位置的元素。
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    // 两个数据源分区数量必须要一致
    val dataRDD1: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val dataRDD2: RDD[Int] = sc.makeRDD(List(3, 4, 5, 6), 2)
    val dataRDD12: RDD[(Int, Int)] = dataRDD1.zip(dataRDD2)
    dataRDD12.collect().foreach(println)
    // 两个数据源的分区内的数据数量要保持一致
    val dataRDD3: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5, 6), 2)
    val dataRDD4: RDD[Int] = sc.makeRDD(List(3, 4, 5, 1, 5, 6), 2)
    val dataRDD34: RDD[(Int, Int)] = dataRDD3.zip(dataRDD4)
    dataRDD34.collect().foreach(println)
    sc.stop()

  }

}
