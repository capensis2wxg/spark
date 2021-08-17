package rdd.rddPartitions

/*
    @author wxg
    @date 2021/7/22-21:24
 */
object MapPart {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    //	mapPartitions可以以分区为单位进行数据转换操作，但是会将整个分区数据加载到内存当中进行引用
    //	因此，处理完的数据是不会被释放的，存在对象的引用
    //	当内存较小，数据量较大时，会出现内存的溢出
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val mapRdd: RDD[Int] = rdd.mapPartitions((iter: Iterator[Int]) => {
      println("------------------------")
      iter.map(_ * 2)
    })
    mapRdd.collect().foreach(println)
    sc.stop()
  }

}
