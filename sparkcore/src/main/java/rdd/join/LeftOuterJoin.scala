package rdd.join

/*
    @author wxg
    @date 2021/8/1-11:56
 */
object LeftOuterJoin {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
		➢  函数签名
	   		def leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
	   	➢  函数说明
	   		类似于 SQL 语句的左外连接
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
          ("b", 2) //, ("c", 3))
        )
      )

    val rdd2: RDD[(String, Int)] =
      sc.makeRDD(
        List(("a", 4), ("a", 5), ("c", 6), ("d", 7))
      )

//    val leftJoin1: RDD[(String, (Int, Option[Int]))] = rdd1.leftOuterJoin(rdd2)
    val leftJoin2: RDD[(String, (Option[Int], Int))] = rdd1.rightOuterJoin(rdd2)
//    leftJoin1.collect().foreach(println)
    leftJoin2.collect().foreach(println)
  }

}
