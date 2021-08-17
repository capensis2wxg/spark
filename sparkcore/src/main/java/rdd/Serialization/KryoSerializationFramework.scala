package rdd.Serialization

/*
    @author wxg
    @date 2021/8/4-10:07
 */
object KryoSerializationFramework {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.{SparkConf, SparkContext}
    val conf: SparkConf = new SparkConf()
      .setAppName("SerDemo")
      .setMaster("local[*]")
      // 替换默认的序列化机制
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      // 注册需要使用 kryo 序列化的自定义类
      .registerKryoClasses(Array(classOf[Searcher]))

    val sc = new SparkContext(conf)

    val rdd: RDD[String] =
      sc.makeRDD(Array("hello world", "hello atguigu", "atguigu", "hahah"), 2)
    val searcher = new Searcher("hello")
    val result: RDD[String] = searcher.getMatchedRDD1(rdd)
    result.collect.foreach(println)
  }
}
case class Searcher(val query: String) {

  import org.apache.spark.rdd.RDD

  def isMatch(s: String): Boolean = {
    s.contains(query)
  }

  def getMatchedRDD1(rdd: RDD[String]): RDD[String] = {
    rdd.filter(isMatch)
  }

  def getMatchedRDD2(rdd: RDD[String]): RDD[String] = {
    val q: String = query
    rdd.filter((_: String).contains(q))
  }

}
