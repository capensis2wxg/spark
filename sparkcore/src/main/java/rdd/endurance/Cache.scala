package rdd.endurance

/*
    @author wxg
    @date 2021/8/4-13:45
 */
object Cache {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
			RDD 通过 Cache 或者 Persist 方法将前面的计算结果缓存，默认情况下会把数据以缓存
		在 JVM 的堆内存中。但是并不是这两个方法被调用时立即缓存，而是触发后面的 action 算
		子时，该 RDD 将会被缓存在计算节点的内存中，并供后面重用。
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.storage.StorageLevel
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val list = List("hello scala")
    val rdd: RDD[String] = sc.makeRDD(list)
    val flatRDD: RDD[String] = rdd.flatMap(_.split(","))
    val mapRDD: RDD[(String, Int)] = flatRDD.map { word: String =>
      { //  如果不使用持久化技术而继续重用RDD，那么这条语句就会被重复执行
        println("=====" * 10)
        (word, 1)
      }
    }
    //	cache 默认持久化的操作，只能够数据保存早内存中，如果想要保存到磁盘文件，需要更改存储级别
    mapRDD.cache()
    mapRDD.persist(StorageLevel.DISK_ONLY)

    val reduceRDD: RDD[(String, Int)] = mapRDD.reduceByKey((_: Int) + (_: Int))
    //	持久化操作是在行动算子执行时完成的
    reduceRDD.collect().foreach(println)
    println("++++++" * 10)

    /*
   		缓存有可能丢失，或者存储于内存的数据由于内存不足而被删除，RDD 的缓存容错机制保证了即使缓存丢失也能保证计算的正确执行。
    通过基于 RDD 的一系列转换，丢失的数据会被重算，由于 RDD 的各个 Partition 是相对独立的，因此只需要计算丢失的部分即可，
	并不需要重算全部 Partition。 Spark 会自动对一些 Shuffle 操作的中间数据做持久化操作(比如：rdd.reduceByKey)。
	这样做的目的是为了当一个节点 Shuffle 失败了避免重新计算整个输入。
	但是，在实际使用的时候，如果想重用数据，仍然建议调用 persist 或 cache。

      	在RDD中，对象可以重用，但是数据不可以重用。如果一个RDD需要重复使用，那么就需要从头再次执行来获取数据。
	在数据执行时间较长，或数据比较重要的场合也可以采用持久化操作，即RDD的持久化操作不一定只是为了重用
     */
    val groupRDD: RDD[(String, Iterable[Int])] = mapRDD.groupByKey()
    groupRDD.collect().foreach(println)

  }

}
