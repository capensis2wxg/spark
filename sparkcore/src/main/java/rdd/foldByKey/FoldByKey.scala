package rdd.foldByKey

/*
    @author wxg
    @date 2021/8/1-10:26
 */
object FoldByKey {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    /*
		如果聚合计算时，分区内和分区间计算规则相同，spark提供了简化的方法
     */
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    val rdd: RDD[(String, Int)] =
      sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("a", 4)))

    rdd.foldByKey(0)(_ + _).collect().foreach(println)

  }

}
