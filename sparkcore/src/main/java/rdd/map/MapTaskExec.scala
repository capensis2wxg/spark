package rdd.map

/*
    @author wxg
    @date 2021/7/22-21:14
 */
object MapTaskExec {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    // 只有一个分区时，分区内的数据的执行是有序的，即前一个数据的计算逻辑必须全部执行结束；如果分区个数大于1,则分区内的数据顺序执行，分区之间的数据并行执行，即是无序执行的
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 1)
    val mapRdd: RDD[Int] = rdd.map(num => {
      println(".........." + num)
      num
    })

    val mapRdd1: RDD[Int] = mapRdd.map(num => {
      println("---------" + num)
      num
    })
    mapRdd1.collect()

    sc.stop()
  }

}
