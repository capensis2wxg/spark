package rdd.map
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
/*
    @author wxg
    @date 2021/7/22-20:53
 */
object MapCut {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    //  读取文件
    val rdd: RDD[String] = sc.textFile("sparkcore/data/apache.log")
    val cut: RDD[String] = rdd.map((line: String) => {
      val strings: Array[String] = line.split(" ")
      strings(6)
    })
    cut.collect().foreach(println)
  }

}
