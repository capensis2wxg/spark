package rdd.Serialization

/*
    @author wxg
    @date 2021/8/4-9:44
 */
object Serialization {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.{SparkConf, SparkContext}
    //1.创建 SparkConf 并设置 App 名称
    val conf: SparkConf =
      new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建 SparkContext，该对象是提交 Spark App 的入口
    val sc: SparkContext = new SparkContext(conf)

    //3.创建一个 RDD
    val rdd: RDD[String] =
      sc.makeRDD(Array("hello world", "hello spark", "hive", "atguigu"))

    //3.1 创建一个 Search 对象
    val search = new Search("hello")

    //3.2 函数传递，打印：ERROR Task not serializable
    //search.getMatch1(rdd).collect().foreach(println)

    //3.3 属性传递，打印：ERROR Task not serializable
    search.getMatch2(rdd).collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}

//  查询对象
//  类的构造参数其实就是类的属性，构造参数如果传入算子内也要进行闭包检测，其实就是对类进行闭包检测
class Search(query: String) {

  import org.apache.spark.rdd.RDD

  def isMatch(s: String): Boolean = {
    s.contains(query) //省略了this
  }

  // 函数序列化案例
  def getMatch1(rdd: RDD[String]): RDD[String] = {
    //rdd.rdd.filter(this.isMatch)
    rdd.filter(isMatch)

  }

  // 属性序列化案例
  def getMatch2(rdd: RDD[String]): RDD[String] = {
    //rdd.rdd.filter(x => x.contains(this.query))
    //rdd.rdd.filter((x: String) => x.contains(query))
    val q: String = query
    //如果这里的q使用的是普通字符串（普通字符串可以进行序列化），不是类的字符串属性，那么闭包检测就可以通过
    rdd.filter((x: String) => x.contains(q))
  }

}
