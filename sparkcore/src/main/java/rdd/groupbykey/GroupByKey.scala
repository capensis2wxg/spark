package rdd.groupbykey

/*
    @author wxg
    @date 2021/8/1-9:35
 */
object GroupByKey {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
	  ➢  函数签名
	  def groupByKey(): RDD[(K, Iterable[V])]
	  def groupByKey(numPartitions: Int): RDD[(K, Iterable[V])]
	  def groupByKey(rdd.partitioner: Partitioner): RDD[(K, Iterable[V])]
	  ➢  函数说明
	  将数据源的数据根据 key 对 value 进行分组
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.HashPartitioner
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    /*
		从 shuffle  的角度：rdd.reduceByKey 和 groupByKey 都存在 shuffle 的操作，但是 rdd.reduceByKey
	可以在 shuffle 前对分区内相同 key 的数据进行预聚合（combine）功能，这样会减少落盘的
	数据量，而 groupByKey 只是进行分组，不存在数据量减少的问题，rdd.reduceByKey 性能比较高。
		从功能的角度：rdd.reduceByKey 其实包含分组和聚合的功能。GroupByKey 只能分组，不能聚
	合，所以在分组聚合的场合下，推荐使用 rdd.reduceByKey，如果仅仅是分组而不需要聚合。那么还是只能使用 groupByKey
     */
    val dataRDD: RDD[(String, Int)] =
      sc.makeRDD(List(("a", 1), ("a", 2), ("a", 3), ("b", 4)))
    /*
     groupRDD:  将数据源中的数据，相同key的数据分在一个组中，形成一个对偶元组；
                  元组中的第一个元素就是Key
                  元组中的第二个元素就是相同key的value的集合
     */

    /*
      groupByKey会导致数据打乱重组，存在shuffle操作；在Spark中，shuffle操作必须落盘处理，不能在内存中等待数据，这有可能导致内存溢出。也就是说，
    shuffle操作的性能非常低。
      但是，reduceByKey支持分区内预聚合功能，可以有效减少shuffle时落盘的数据量
     */

    val groupRDD: RDD[(String, Iterable[Int])] = dataRDD.groupByKey()
    groupRDD.collect().foreach(println)

    val groupRDD2: RDD[(String, Iterable[(String, Int)])] =
      dataRDD.groupBy(_._1)
    groupRDD2.collect().foreach(println)

    //	关闭资源
    sc.stop()

  }

}
