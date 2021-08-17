package rdd.DoubleValue

import org.apache.spark.{SparkConf, SparkContext}

/*
    @author wxg
    @date 2021/7/31-21:30
 */
object SetOperations {
  def main(args: Array[String]): Unit = {}
  import org.apache.spark.rdd.RDD
  val sparkConf: SparkConf =
    new SparkConf().setMaster("local[*]").setAppName("rdd/map")
  val sc = new SparkContext(sparkConf)

  //交集的数据类型必须完全一致
  val dataRDD1: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
  val dataRDD2: RDD[Int] = sc.makeRDD(List(3, 4, 5, 6))
  val dataRDD12: RDD[Int] = dataRDD1.intersection(dataRDD2)
  dataRDD12.collect().foreach(println)

  //并集不会去除重复数据,数据类型也必须要一致
  val dataRDD3: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
  val dataRDD4: RDD[Int] = sc.makeRDD(List(3, 4, 5, 6))
  val dataRDD34: RDD[Int] = dataRDD3.union(dataRDD4)
  dataRDD34.collect().foreach(println)

  //差集的数据类型也必须要一致
  val dataRDD5: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
  val dataRDD6: RDD[Int] = sc.makeRDD(List(3, 4, 5, 6))
  val dataRDD56: RDD[Int] = dataRDD5.subtract(dataRDD6)
  dataRDD56.collect().foreach(println)
}
