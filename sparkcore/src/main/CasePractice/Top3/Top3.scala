package Top3

/*
    @author wxg
    @date 2021/8/3-10:40
 */
object Top3 {
  import org.apache.spark.{SparkConf, SparkContext}
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    //	1、获取原始数据：时间戳，省份，城市，用户，广告
    val dataRDD: RDD[String] = sc.textFile("sparkcore/data/agent.log")
    //	2、将原始数据进行结构的转换，方便统计：时间戳，省份，城市，用户，广告 =>（（省份，广告），1）
    val mapRDD: RDD[((String, String), Int)] = dataRDD.map((line: String) => {
      val strings: Array[String] = line.split(" ")
      ((strings(1), strings(4)), 1)
    })
    //	3、将转换结构后的数据，进行分组聚合 （（省份，广告），1）=> （（省份，广告），sum）
    val reduceRDD: RDD[((String, String), Int)] = mapRDD.reduceByKey(_ + _)
    //	4、将聚合后的结果再次进行结构的转换  （（省份，广告），sum）=> （省份，（广告，sum））
    val newMapRDD: RDD[(String, (String, Int))] = reduceRDD.map {
      case ((prv, ad), sum) => {
        (prv, (ad, sum))
      }
    }
    //	5、将转换后的数据结构根据省份进行分组 （省份， 【（广告A，sumA），（广告B， sumB）】）
    val groupRDD: RDD[(String, Iterable[(String, Int)])] =
      newMapRDD.groupByKey()
    //	6、将分组后的数据进行组内排序（降序），然后取出前三个
    val retRDD: RDD[(String, List[(String, Int)])] =
      groupRDD.mapValues((iter: Iterable[(String, Int)]) => {
        iter.toList.sortBy((_: (String, Int))._2)(Ordering.Int.reverse).take(3)
      })
    //	7、采集数据打印到控制台
    retRDD.collect().foreach(println)

    //	8、关闭资源
    sc.stop()
  }
}
