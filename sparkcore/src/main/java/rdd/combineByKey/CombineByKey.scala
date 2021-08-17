package rdd.combineByKey

/*
    @author wxg
    @date 2021/8/1-10:53
 */
object CombineByKey {

  import org.apache.spark.{SparkConf, SparkContext}
  /*
		➢  函数签名
		   def rdd.combineByKey[C](
			 createCombiner: V => C,	将相同key的第一个数据进行结构的转换，实现求和操作
			 mergeValue: (C, V) => C,	分区内的计算规则
			 mergeCombiners: (C, C) => C): RDD[(K, C)]	分区间的计算规则
  		➢  函数说明
  			最通用的对 key-value 型 rdd 进行聚集操作的聚集函数（aggregation function）。
  			类似于aggregate()，rdd.combineByKey()允许用户返回值的类型与输入不一致。
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    val rdd: RDD[(String, Int)] =
      sc.makeRDD(
        List(("a", 2), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("b", 6))
      )

    //获得相同key的数据的平均值
    val comRDD: RDD[(String, (Int, Int))] = rdd
      .combineByKey(
        v => { (v, 1) },
        (t: (Int, Int), v: Int) => {
          (t._1 + v, t._2 + 1)
        },
        (t1: (Int, Int), t2: (Int, Int)) => {
          (t1._1 + t2._1, t1._2 + t2._2)
        }
      )

    val mapRDD: RDD[(String, Int)] = comRDD.mapValues {
      case (num, cnt) => {
        num / cnt
      }
    }

    mapRDD.collect().foreach(println)

    sc.stop()
  }

}
