package rdd.rddBuilder

/*
    @author wxg
    @date 2021/7/22-17:02
 */
object RddFiles {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.{SparkConf, SparkContext}
    // 准备环境
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(sparkConf)

    //	从文件中创建RDD，将文件中的数据作为数据的处理源，注意Path: 基准路径为当前环境的根路径，或者也可以写相对路径
//    val rdd: RDD[String] = sc.textFile("sparkcore/data/wordCount1")
    //	使用通配符
//    val rdd: RDD[String] = sc.textFile("sparkcore/data/wordCount*")
    //	使用目录
//    val rdd: RDD[String] = sc.textFile("sparkcore/data/")
    //	打印文件路径和数据
//    val rdd: RDD[(String, String)] = sc.wholeTextFiles("sparkcore/data")
    //	读取集群中的数据，以文件为单位，结果是一个元组(文件路径，文件内容); 而textFile则是以行为单位
    val rdd: RDD[(String, String)] =
      sc.wholeTextFiles("hdfs://hadoop102:9000/*.txt")
    rdd.collect().foreach(println)
    //	关闭环境
    sc.stop()
  }

}
