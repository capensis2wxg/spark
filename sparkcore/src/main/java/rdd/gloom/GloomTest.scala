package rdd.gloom

/*
    @author wxg
    @date 2021/7/31-19:14
 */

/**
  * ➢  函数签名
        def rdd.gloom(): RDD[Array[T]]
    ➢  函数说明
          将同一个分区的数据直接转换为相同类型的内存数组进行处理，分区不变
  */
object GloomTest {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    //List => Int
    //Int => List
    val dataRDD: RDD[Array[Int]] = rdd.glom()
    dataRDD.collect().foreach((data: Array[Int]) => println(data.mkString(",")))

    //求出每个分区的最大值，并进行相加
    val maxRDD: RDD[Int] = dataRDD.map(array => {
      array.max
    })
    println(maxRDD.collect().sum)
  }
}
