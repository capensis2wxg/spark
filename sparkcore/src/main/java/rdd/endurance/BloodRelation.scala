package rdd.endurance

/*
    @author wxg
    @date 2021/8/4-15:01
 */
object BloodRelation {
  /*
	cache:	将数据临时存储在内存中进行数据重用，会在血缘关系中添加新的依赖。一旦出现问题，可以重头读取数据
	persist：将数据临时存储在磁盘文件中进行数据重用，涉及到磁盘IO，性能较低。但是，数据比较安全。如果作业执行完毕，临时执行的数据文件就会丢失
	checkpoint：将数据长久地保存在磁盘文件中进行数据重用，涉及到磁盘IO，性能较低。但是，数据比较安全。
				为了保证数据安全，所以一般情况下，会独立执行做业。为了能够提高效率，一般情况下，是需要和cache来联合使用。在执行过程中，会
				切断血缘关系，重新建立新的血缘关系。checkpoint等同于改变数据源。
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.{SparkConf, SparkContext}
    import org.apache.spark.rdd.RDD
    import org.apache.spark.storage.StorageLevel
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    // 设置检查点路径
    //sc.setCheckpointDir("./checkpoint1")
    val list = List("hello scala")
    val rdd: RDD[String] = sc.makeRDD(list)
    val flatRDD: RDD[String] = rdd.flatMap(_.split(","))
    val mapRDD: RDD[(String, Int)] = flatRDD.map { word: String =>
      {
        println("=====" * 10)
        (word, 1)
      }
    }

    mapRDD.cache()
    //mapRDD.checkpoint()
    //mapRDD.persist(StorageLevel.DISK_ONLY)
    println(mapRDD.toDebugString)

    val reduceRDD: RDD[(String, Int)] = mapRDD.reduceByKey((_: Int) + (_: Int))

    reduceRDD.collect().foreach(println)
    println("++++++" * 10)
    println(mapRDD.toDebugString)

    val groupRDD: RDD[(String, Iterable[Int])] = mapRDD.groupByKey()
    groupRDD.collect().foreach(println)
  }
}
