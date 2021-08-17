package Top10

/*
    @author wxg
    @date 2021/8/7-9:35
 */
object spark01_hotCategory {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    //	TODO: TOP10热门商品种类
    import org.apache.spark.rdd.RDD

    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sc = new SparkContext(sparkConf)

    //  1、读取原始数据日志
    val actionRDD: RDD[String] =
      sc.textFile("sparkcore/data/user_visit_action.txt")
    //  2、统计品类的点击数量（品类ID，点击数量）
    val clickActionRDD: RDD[String] = actionRDD.filter((action: String) => {
      // actionRDD可能重复使用
      val strings: Array[String] = action.split("_")
      strings(6) != "-1"
    })

    val clickCountRDD: RDD[(String, Int)] =
      clickActionRDD
        .map((action: String) => {
          val strings: Array[String] = action.split("_")
          (strings(6), 1)
        })
        .reduceByKey((_: Int) + (_: Int))

    //  3、统计品类的下单数量（品类ID，下单数量）
    val orderActionRDD: RDD[String] = actionRDD.filter((action: String) => {
      val strings: Array[String] = action.split("_")
      strings(8) != "null"
    })
    val orderCountRDD: RDD[(String, Int)] = orderActionRDD
      .flatMap((action: String) => {
        val strings: Array[String] = action.split("_")
        val cid: String = strings(8)
        val cids: Array[String] = cid.split(",")
        cids.map((_: String, 1))
      })
      .reduceByKey((_: Int) + (_: Int))

    //  4、统计品类的支付ID（品类ID，支付数量）
    val payActionRDD: RDD[String] = actionRDD.filter((action: String) => {
      val strings: Array[String] = action.split("_")
      strings(10) != "null"
    })
    val payCountRDD: RDD[(String, Int)] = payActionRDD
      .flatMap((action: String) => {
        val strings: Array[String] = action.split("_")
        val cid: String = strings(10)
        val cids: Array[String] = cid.split(",")
        cids.map((_: String, 1))
      })
      .reduceByKey((_: Int) + (_: Int))

    //  5、将品类进行排序，并且取前10名，点击数量排序、下单数量排序、支付数量排序
    //  利用元组进行排序  先比较第一个、再比较第二个、最后比较第三个
    //  （品类ID，（点击数量、下单数量、支付数量））
    val coGroupRDD
        : RDD[(String, (Iterable[Int], Iterable[Int], Iterable[Int]))] =
      //  cogroup有可能存在shuffle
      clickCountRDD.cogroup(orderCountRDD, payCountRDD)
    val analysisRDD: RDD[(String, (Int, Int, Int))] = coGroupRDD.mapValues {
      case (clickIter, orderIter, payIter) =>
        var clickCount = 0
        if (clickIter.iterator.hasNext) {
          clickCount = clickIter.iterator.next
        }
        var orderCount = 0
        if (orderIter.iterator.hasNext) {
          orderCount = orderIter.iterator.next
        }
        var payCount = 0
        if (payIter.iterator.hasNext) {
          payCount = payIter.iterator.next
        }
        (clickCount, orderCount, payCount)
    }
    val retRDD: Array[(String, (Int, Int, Int))] = analysisRDD
      .sortBy((_: (String, (Int, Int, Int)))._2, ascending = false)
      .take(10)

    //  6、将结果打印在控制台上
    retRDD.foreach(println)

    sc.stop()
  }

}
