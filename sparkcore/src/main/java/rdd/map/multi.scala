package rdd.map

/*
    @author wxg
    @date 2021/7/22-20:32
 */
object multi {
  import org.apache.spark.SparkConf

  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkContext
    import org.apache.spark.rdd.RDD

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    //	算子 rdd.map
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    //	自定义函数
//    def func(num: Int): Int = {
//      num * 2
//    }

//    val ret: RDD[Int] = rdd.rdd.map(func)

    //	使用匿名函数
    val ret: RDD[Int] = rdd.map(_ * 2)
    ret.collect().foreach(println)
    sc.stop()
  }
}
