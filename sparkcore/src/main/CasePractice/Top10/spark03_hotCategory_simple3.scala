package Top10

/*
    @author wxg
    @date 2021/8/7-11:08
 */
object spark03_hotCategory_simple3 {

  import org.apache.spark.util.AccumulatorV2
  import org.apache.spark.{SparkConf, SparkContext}

  import scala.collection.mutable

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    //  Q: reduceByKey聚合算子需要进行大量的shuffle操作
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("hotCategory")
    val sc = new SparkContext(sparkConf)

    //  1、读取原始数据日志
    val actionRDD: RDD[String] =
      sc.textFile("sparkcore/data/user_visit_action.txt")
    //	2、创建累加器并注册
    val acc = new HotCategoryAccumulator()
    sc.register(acc, "hotCategory")

    //	3、将数据转换结构	（品类ID，（1， 0， 0）），（品类ID，（0， 1， 0）），（品类ID，（0， 0， 1））
    actionRDD.foreach((action: String) => {
      val strings: Array[String] = action.split("_")
      if (strings(6) != "-1") {
        //  点击的场合
        acc.add(strings(6), "click")
      } else if (strings(8) != "null") {
        //  下单的场合
        val ids: Array[String] = strings(8).split(",")
        ids.foreach((id: String) => acc.add((id, "order")))
      } else if (strings(10) != "null") {
        //  支付的场合
        val ids: Array[String] = strings(10).split(",")
        ids.foreach((id: String) => acc.add((id, "pay")))
      }
    })

    val accVal: mutable.Map[String, HotCategory] = acc.value
    val categories: Iterable[HotCategory] = accVal.values

    //	4、排序
    val sortRDD: List[HotCategory] =
      categories.toList.sortWith((left: HotCategory, right: HotCategory) => {
        if (left.clickCount > right.clickCount) {
          true
        } else if (left.clickCount == right.clickCount) {
          if (left.orderCount > right.orderCount) {
            true
          } else if (left.orderCount == right.orderCount) {
            left.payCount > right.payCount
          } else false
        } else false
      })

    //  5、将结果打印在控制台上
    sortRDD.take(10).foreach(println)
  }

  case class HotCategory(
      cid: String,
      var clickCount: Int,
      var orderCount: Int,
      var payCount: Int
  )

  /**
    *	自定义累加器
    */
  class HotCategoryAccumulator
      extends AccumulatorV2[
        (String, String),
        mutable.Map[String, HotCategory]
      ] {

    private val hcMap: mutable.Map[String, HotCategory] =
      mutable.Map[String, HotCategory]()
    override def isZero: Boolean = hcMap.isEmpty

    override def copy()
        : AccumulatorV2[(String, String), mutable.Map[String, HotCategory]] =
      new HotCategoryAccumulator()

    override def reset(): Unit = hcMap.clear()

    override def add(v: (String, String)): Unit = {
      val cid: String = v._1
      val actionType: String = v._2
      val category: HotCategory =
        hcMap.getOrElse(cid, HotCategory(cid, 0, 0, 0))
      if (actionType == "click") { category.clickCount += 1 }
      else if (actionType == "order") { category.orderCount += 1 }
      else if (actionType == "pay") { category.payCount += 1 }
      hcMap.update(cid, category)

    }

    override def merge(
        other: AccumulatorV2[(String, String), mutable.Map[String, HotCategory]]
    ): Unit = {
      val map1: mutable.Map[String, HotCategory] = this.hcMap
      val map2: mutable.Map[String, HotCategory] = other.value
      map2.foreach {
        case (cid, hc) =>
          val category: HotCategory =
            map1.getOrElse(cid, HotCategory(cid, 0, 0, 0))
          category.clickCount += hc.clickCount
          category.orderCount += hc.orderCount
          category.payCount += hc.payCount
          map1.update(cid, category)
      }
    }

    override def value: mutable.Map[String, HotCategory] = hcMap
  }

}
