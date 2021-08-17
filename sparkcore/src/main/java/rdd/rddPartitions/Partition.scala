package rdd.rddPartitions

/*
    @author wxg
    @date 2021/7/22-17:28
 */
object Partition {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.{SparkConf, SparkContext}
    // 准备环境
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("RDD")
//    sparkConf.set("spark.default.parallelism", "5")
    val sc = new SparkContext(sparkConf)
    //	RDD的并行度和分区，makeRDD的第二个参数为分区，默认为cpu核数，可以指定分区个数;也可以在SparkConf中指定
//    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 5)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    //	查看分区文件
    rdd.saveAsTextFile("output")
    rdd.collect().foreach(println)
    //	关闭环境
    sc.stop()
  }

}
