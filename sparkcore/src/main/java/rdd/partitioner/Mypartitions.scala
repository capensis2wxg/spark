package rdd.partitioner

/*
    @author wxg
    @date 2021/8/4-15:24
 */
object Mypartitions {

  import org.apache.spark.{Partitioner, SparkConf, SparkContext}
  /*
	  Spark 目前支持 Hash 分区和 Range 分区，和用户自定义分区。Hash 分区为当前的默认
	  分区。分区器直接决定了 RDD 中分区的个数、RDD 中每条数据经过 Shuffle 后进入哪个分区，进而决定了 Reduce 的个数。
  		➢  只有 Key-Value 类型的 RDD 才有分区器，非 Key-Value 类型的 RDD 分区的值是 None
  		➢  每个 RDD 的分区 ID 范围：0 ~ (numPartitions - 1)，决定这个值是属于那个分区的。
  		Hash 分区：对于给定的 key，计算其 hashCode,并除以分区个数取余
        Range 分区：将一定范围内的数据映射到一个分区中，尽量保证每个分区数据均匀，而且分区间有序
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.makeRDD(
      List(
        ("nba", "------"),
        ("cba", "====="),
        ("wnba", "+++++"),
        ("nba", "-------")
      ),
      3
    )
    val partitionRDD: RDD[(String, String)] =
      rdd.partitionBy(new Defpartition())
    partitionRDD.saveAsTextFile("sparkcore/data/Defpartition")
    sc.stop()
  }

  class Defpartition extends Partitioner {
    override def numPartitions: Int = 3
    /*
			根据数据的Key值返回数据所在的分区索引（从0开始）
     */
    override def getPartition(key: Any): Int = {
      key match {
        case "nba"  => 0
        case "wnba" => 1
        case _      => 2
      }
    }
  }
}
