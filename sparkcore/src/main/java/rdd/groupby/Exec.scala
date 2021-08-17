package rdd.groupby

import org.apache.spark.{SparkConf, SparkContext}

/*
    @author wxg
    @date 2021/7/31-20:03
 */
object Exec {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[String] = sc.textFile("sparkcore/data/apache.log")
    val timeRDD: RDD[(String, Iterable[(String, Int)])] = rdd
      .map((line: String) => {
        import java.text.SimpleDateFormat
        import java.util.Date
        val strings: Array[String] = line.split(" ")
        val time: String = strings(3)
        val sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss")
        val date: Date = sdf.parse(time)
        val sdf2 = new SimpleDateFormat("HH")
        val hour: String = sdf2.format(date)
        (hour, 1)
      })
      .groupBy(_._1)

    timeRDD
      .map {
        case (hour, iter) => {
          (hour, iter.size)
        }
      }
      .collect()
      .foreach(println)

    sc.stop()
  }
}
