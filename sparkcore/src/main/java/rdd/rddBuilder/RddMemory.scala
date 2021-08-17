package rdd.rddBuilder

/*
    @author wxg
    @date 2021/7/22-15:42
 */
object RddMemory {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.{SPARK_BRANCH, SparkConf, SparkContext}
    // 准备环境
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    // 创建RDD, 将内存中集合的数据作为处理的数据源
    val seq: Seq[Int] = Seq[Int](1, 2, 3, 4)
    //	并行
//    val rdd: RDD[Int] = sc.parallelize(seq)
    val rdd: RDD[Int] = sc.makeRDD(seq)

    rdd.collect().foreach(println)
    //	关闭环境
    sc.stop()
  }

}
