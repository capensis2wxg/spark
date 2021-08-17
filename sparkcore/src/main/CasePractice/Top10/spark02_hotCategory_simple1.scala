package Top10

/*
    @author wxg
    @date 2021/8/7-10:49
 */
object spark02_hotCategory_simple1 {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD

    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sc = new SparkContext(sparkConf)

    //  1、读取原始数据日志
    val actionRDD: RDD[String] =
      sc.textFile("sparkcore/data/user_visit_action.txt")
    actionRDD.cache()
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

    //  5、将品类进行排序，并且取前10名，点击数量排序、下单数量排序、支付数量排序; 不再使用cogroup
    val rddClick: RDD[(String, (Int, Int, Int))] = clickCountRDD.map {
      case (cid, cnt) => (cid, (cnt, 0, 0))
    }
    val rddOrder: RDD[(String, (Int, Int, Int))] = orderCountRDD.map {
      case (cid, cnt) => (cid, (0, cnt, 0))
    }
    val rddPay: RDD[(String, (Int, Int, Int))] = payCountRDD.map {
      case (cid, cnt) => (cid, (0, 0, cnt))
    }
    //	6、将三个数据源合并在一起，统一进行聚合计算
    val sourceRDD: RDD[(String, (Int, Int, Int))] =
      rddClick.union(rddOrder).union(rddPay)
    val analysisRDD: RDD[(String, (Int, Int, Int))] =
      sourceRDD.reduceByKey((t1: (Int, Int, Int), t2: (Int, Int, Int)) => {
        (t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
      })
    val retRDD: Array[(String, (Int, Int, Int))] = analysisRDD
      .sortBy((_: (String, (Int, Int, Int)))._2, ascending = false)
      .take(10)

    //  6、将结果打印在控制台上
    retRDD.foreach(println)

    sc.stop()
  }

}
