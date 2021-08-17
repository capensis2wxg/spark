package Top10

/*
    @author wxg
    @date 2021/8/7-11:08
 */
object Spark04_hotCategory2_simple1 {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    //  Q: reduceByKey聚合算子需要进行大量的shuffle操作
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sc = new SparkContext(sparkConf)

    //  1、读取原始数据日志
    val actionRDD: RDD[String] =
      sc.textFile("sparkcore/data/user_visit_action.txt")
    actionRDD.cache()

    /**
      * 求出top10
      * @param actionRDD
      * @return
      */
    def top10(actionRDD: RDD[String]): Array[String] = {
      //	将数据转换结构	（品类ID，（1， 0， 0）），（品类ID，（0， 1， 0）），（品类ID，（0， 0， 1））
      val flatRDD: RDD[(String, (Int, Int, Int))] =
        actionRDD.flatMap((action: String) => {
          val strings: Array[String] = action.split("_")
          if (strings(6) != "-1") {
            //  点击的场合
            List((strings(6), (1, 0, 0)))
          } else if (strings(8) != "null") {
            //  下单的场合
            val ids: Array[String] = strings(8).split(",")
            ids.map((id: String) => (id, (0, 1, 0)))
          } else if (strings(10) != "null") {
            //  支付的场合
            val ids: Array[String] = strings(10).split(",")
            ids.map((id: String) => (id, (0, 0, 1)))
          } else Nil
        })

      //  将相同品类ID的数据进行分组聚合（品类ID，（点击数量， 下单数量， 支付数量））
      val analysisRDD: RDD[(String, (Int, Int, Int))] =
        flatRDD.reduceByKey((t1: (Int, Int, Int), t2: (Int, Int, Int)) =>
          (t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
        )
      //  排序
      analysisRDD
        .sortBy((_: (String, (Int, Int, Int)))._2, ascending = false)
        .take(10)
        .map((_: (String, (Int, Int, Int)))._1)
    }
    val retIds: Array[String] = top10(actionRDD)
    //	2、过滤原始数据、保留点击行为
    val filterRDD: RDD[String] = actionRDD.filter((action: String) => {
      val strings: Array[String] = action.split("")
      if (strings(6) != "-1") {
        retIds.contains(strings(6))
      } else false
    })
    //	3、根据品类ID和sessionId进行点击量的统计
    val reduceRDD: RDD[((String, String), Int)] =
      filterRDD
        .map((action: String) => {
          val strings: Array[String] = action.split("_")
          ((strings(6), strings(2)), 1)
        })
        .reduceByKey((_: Int) + (_: Int))
    //  4、将统计的结果进行结构的转换
    val mapRDD: RDD[(String, (String, Int))] = reduceRDD.map {
      case ((cid, sid), sum) => (cid, (sid, sum))
    }
    //  5、相同的品类进行分组
    val groupRDD: RDD[(String, Iterable[(String, Int)])] = mapRDD.groupByKey()
    //  6、将分组后的数据进行点击量的排名，然后取出前十个
    val retRDD: RDD[(String, List[(String, Int)])] =
      groupRDD.mapValues((iter: Iterable[(String, Int)]) =>
        iter.toList.sortBy((_: (String, Int))._2)(Ordering.Int.reverse).take(10)
      )
    retRDD.collect().foreach(println)

    sc.stop()

  }

}
