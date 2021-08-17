package rdd.aggregateByKey

/*
    @author wxg
    @date 2021/8/1-10:10
 */
object AggregateByKey {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
		➢  函数签名
			def rdd.aggregateByKey[U: ClassTag](zeroValue: U)(seqOp: (U, V) => U,
			  combOp: (U, U) => U): RDD[(K, U)]
		➢  函数说明
			将数据根据不同的规则进行分区内计算和分区间计算
			由函数签名可以看出，函数返回的数据类型由传入的初始值类型一致
   */

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    val rdd: RDD[(String, Int)] =
      sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("a", 4)))
    /*
		 rdd.aggregateByKey 算子是函数柯里化，存在两个参数列表
			1. 第一个参数列表中的参数表示初始值,主要用于与第一个带有key的value进行分区内计算
			2. 第二个参数列表中含有两个参数
				2.1 第一个参数表示分区内的计算规则
				2.2 第二个参数表示分区间的计算规则
     */
    val aggRDD: RDD[(String, Int)] = rdd.aggregateByKey(0)(
      (x: Int, y: Int) => math.max(x, y),
      (x: Int, y: Int) => x + y
    )
    aggRDD.collect().foreach(println)

    val aggRDD2: RDD[(String, Int)] = rdd.aggregateByKey(9)(
      math.max(_: Int, _: Int),
      (_: Int) + (_: Int)
    )
    aggRDD2.collect().foreach(println)

    //根据相同的规则进行计算
    val aggRDD3: RDD[(String, Int)] = rdd.aggregateByKey(9)(_ + _, _ + _)
    aggRDD3.collect().foreach(println)

    val rdd2: RDD[(String, Int)] =
      sc.makeRDD(
        List(("a", 1), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("b", 6))
      )
    //获得相同key的数据的平均值
    val aggRDD4: RDD[(String, (Int, Int))] = rdd2
      .aggregateByKey((0, 0))(
        (t: (Int, Int), v: Int) => {
          (t._1 + v, t._2 + 1)
        },
        (t1: (Int, Int), t2: (Int, Int)) => {
          (t1._1 + t2._1, t2._1 + t2._2)
        }
      )
    //	String：key;     Int: value
    val mapRDD: RDD[(String, Int)] = aggRDD4.mapValues {
      case (num, cnt) => {
        num / cnt
      }
    }

    mapRDD.collect().foreach(println)

    sc.stop()

  }

}
