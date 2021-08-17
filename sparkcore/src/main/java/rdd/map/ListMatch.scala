package rdd.map

/*
    @author wxg
    @date 2021/7/23-9:44
 */
object ListMatch {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Any] = sc.makeRDD(List(List(1, 2), 5, List(3, 4)))
    val flatMap: RDD[Any] = rdd.flatMap { data =>
      {
        data match {
          case list: List[_] => list
          case data          => List(data)
        }
      }

    }
    flatMap.collect().foreach(println)
    sc.stop()
  }

}
