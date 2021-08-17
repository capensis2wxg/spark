package rdd.rddIO

/*
    @author wxg
    @date 2021/8/4-15:44
 */
object ReadSave {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
		Spark 的数据读取及数据保存可以从两个维度来作区分：文件格式以及文件系统。
	  文件格式分为：text 文件、csv 文件、sequence 文件以及 Object 文件；
	  文件系统分为：本地文件系统、HDFS、HBASE 以及数据库。
   */
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    //    val rdd: RDD[(String, Int)] =
    //      sc.makeRDD(List(("a", 1), ("b", 2), ("c", 2), ("a", 3)))
    //    rdd.saveAsTextFile("readT")
    //    rdd.saveAsObjectFile("readO")
    //    rdd.saveAsSequenceFile("readS")

    //  读数据
    val rdd1 = sc.textFile("readT")
    println(rdd1.collect().mkString(","))
    /*
			对象文件是将对象序列化后保存的文件，采用 Java 的序列化机制。可以通过 objectFile[T:
		ClassTag](path)函数接收一个路径，读取对象文件，返回对应的 RDD，也可以通过调用
		saveAsObjectFile()实现对对象文件的输出。因为是序列化所以要指定类型。
     */
    val rdd2 = sc.objectFile[(String, Int)]("readO")
    println(rdd2.collect().mkString(","))

    /*
		SequenceFile 文件是 Hadoop 用来存储二进制形式的 key-value 对而设计的一种平面文件(Flat
	File)。在 SparkContext 中，可以调用 sequenceFile[keyClass, valueClass](path)。
     */
    val rdd3 = sc.sequenceFile[String, Int]("readS")
    println(rdd3.collect().mkString(","))
    sc.stop()

  }

}
