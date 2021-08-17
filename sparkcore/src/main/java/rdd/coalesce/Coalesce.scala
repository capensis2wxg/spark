package rdd.coalesce

/*
    @author wxg
    @date 2021/7/31-21:02
 */
object Coalesce {

  import org.apache.spark.{SparkConf, SparkContext}

  /**
    * ➢  函数签名
    *def rdd.coalesce(numPartitions: Int, shuffle: Boolean = false,
    *partitionCoalescer: Option[PartitionCoalescer] = Option.empty)
    *(implicit ord: Ordering[T] = null) : RDD[T]
    *➢  函数说明
    *根据数据量缩减分区，用于大数据集过滤后，提高小数据集的执行效率
    *当 spark 程序中，存在过多的小任务的时候，可以通过 rdd.coalesce 方法，收缩合并分区，减少分区的个数，减小任务调度成本
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val dataRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 1, 2), 3)
    //  缩减分区可以使用coalesce，但是要想保证数据均衡，必须使用shuffle，同时也可以进行扩大分区，此时必须知道那个shuffle
    val dataRDD1: RDD[Int] = dataRDD.coalesce(2)
    dataRDD1.saveAsTextFile("rdd/coalesce")

    //也可以使用repartition来进行扩大分区
    /*
    ➢  函数签名
        def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T]
    ➢  函数说明
        该操作内部其实执行的是 rdd.coalesce 操作，参数 shuffle 的默认值为 true。 无论是将分区数多的
        RDD 转换为分区数少的 RDD，还是将分区数少的 RDD 转换为分区数多的 RDD，repartition 操作都可以完成，因为无论如何都会经 shuffle 过程。
     */
    dataRDD.repartition(6).saveAsTextFile("repartition")
  }
}
