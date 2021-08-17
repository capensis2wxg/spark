package rdd.join

/*
    @author wxg
    @date 2021/8/1-11:46
 */
object Join {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    /*
		 ➢  函数签名
			 def rdd.join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
		 ➢  函数说明
			在类型为(K,V)和(K,W)的 RDD 上调用，返回一个相同 key 对应的所有元素连接在一起的(K,(V,W))的 RDD
     */
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    val rdd1: RDD[(String, Int)] =
      sc.makeRDD(
        List(("a", 1), ("b", 2), ("c", 3))
      )

    val rdd2: RDD[(String, Int)] =
      sc.makeRDD(
        List(("a", 4), ("a", 2), ("a", 5), ("c", 6), ("d", 7))
      )

    /*
      两个不同数据源的数据，相同key的value会来连接在一起，形成元组
      如果两个数据源中key没有匹配上，那么数据不会出现在结果中
      如果两个数据源中村子啊多个相同的key，那么会依次进行匹配，形成类似笛卡尔积的结果，数据量会急剧增长
     */

    val joinRDD: RDD[(String, (Int, Int))] = rdd1.join(rdd2)
    joinRDD.collect().foreach(println)

    sc.stop()
  }
}
