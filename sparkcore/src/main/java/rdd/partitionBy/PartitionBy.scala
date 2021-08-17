package rdd.partitionBy

/*
    @author wxg
    @date 2021/8/1-0:01
 */
object PartitionBy {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
  ➢  函数签名
    def rdd.partitionBy(rdd.partitioner: Partitioner): RDD[(K, V)]
  ➢  函数说明
    将数据按照指定 Partitioner 重新进行分区。Spark 默认的分区器是 HashPartitioner
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[(Int, String)] =
      sc.makeRDD(Array((1, "aaa"), (2, "bbb"), (3, "ccc")), 3)
    import org.apache.spark.HashPartitioner
    //隐式转换（二次编译）
    val rdd2: RDD[(Int, String)] = rdd.partitionBy(new HashPartitioner(2))
    rdd2.saveAsTextFile("rdd/partitionBy")

    sc.stop()
  }

}
