package rdd.cogroup

/*
    @author wxg
    @date 2021/8/1-12:03
 */
object CoGroup {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
	   ➢  函数签名
			def rdd.cogroup[W](other: RDD[(K, W)]): RDD[(K, (Iterable[V], Iterable[W]))]
		 ➢  函数说明
			在类型为(K,V)和(K,W)的 RDD 上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的 RDD
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    val rdd1: RDD[(String, Int)] =
      sc.makeRDD(
        List(
          ("a", 1),
          ("b", 2),
          ("e", 3)
          //, ("c", 3))
        )
      )

    val rdd2: RDD[(String, Int)] =
      sc.makeRDD(
        List(("a", 4), ("a", 5), ("c", 6), ("d", 7))
      )

    val coRDD: RDD[(String, (Iterable[Int], Iterable[Int]))] =
      rdd1.cogroup(rdd2)
    coRDD.collect().foreach(println)

    sc.stop()
  }

}
