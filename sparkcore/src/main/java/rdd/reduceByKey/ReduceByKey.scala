package rdd.reduceByKey

/*
    @author wxg
    @date 2021/8/1-0:20
 */
object ReduceByKey {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
	➢  函数签名
	  def rdd.reduceByKey(func: (V, V) => V): RDD[(K, V)]
	  def rdd.reduceByKey(func: (V, V) => V, numPartitions: Int): RDD[(K, V)]
	➢  函数说明
	  可以将数据按照相同的 Key 对 Value 进行聚合，注意：reduceByKey分区内和分区间计算规则是相同的。
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val dataRDD1: RDD[(String, Int)] =
      sc.makeRDD(List(("a", 1), ("a", 1), ("b", 2), ("b", 1), ("c", 3)))
    //	如果reduceByKey中的相同的key只存在一个，那么该数据是不会参与运算的
    val dataRDD2: RDD[(String, Int)] =
      dataRDD1.reduceByKey((x: Int, y: Int) => {
        println(s"x = $x, y = $y")
        x + y
      })
    dataRDD2.collect.foreach(println)
    val dataRDD3: RDD[(String, Int)] = dataRDD1.reduceByKey(_ + _, 2)
    dataRDD3.collect.foreach(println)

    sc.stop()
  }

}
