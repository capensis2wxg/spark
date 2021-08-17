package rdd.groupby

/*
    @author wxg
    @date 2021/7/23-12:27
 */

/**
  * ➢  函数签名
		def groupBy[K](f: T => K)(implicit kt: ClassTag[K]): RDD[(K, Iterable[T])]
➢  函数说明
		将数据根据指定的规则进行分组, 分区默认不变，但是数据会被打乱重新组合，我们将这样的操作称之为 shuffle。
		极限情况下，数据可能被分在同一个分区中一个组的数据在一个分区中，但是并不是说一个分区中只有一个组
  */
object groupBy {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    //	定义一个分组函数，该函数会将每一个数据进行分组判断，根据返回的分组key进行分组，相同的key值的数据会放置在一个组中
    def group(int: Int): Int = int % 2
    val groupRDD: RDD[(Int, Iterable[Int])] = rdd.groupBy(group)
    groupRDD.collect().foreach(println)

    //	分组和分区没有任何关系
    val rdd2: RDD[String] =
      sc.makeRDD(List("Hadoop", "HIve", "Scala", "Spark"), 2)
    rdd2.groupBy((_: String).charAt(0)).collect().foreach(println)
  }
}
