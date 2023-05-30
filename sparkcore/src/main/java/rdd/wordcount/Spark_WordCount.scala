package rdd.wordcount
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/*
    @author wxg
    @date 2021/6/29-14:27
 */

object Spark_WordCount {
	def main(args: Array[String]): Unit = {
		//  获取spark的配置文件
		val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("WordCount")
		//  建立和spark框架的连接
		val sc = new SparkContext(sparkConf)
		//  1、读取文件，获取一行一行的数据
		val lines: RDD[String] = sc.textFile("E:\\IDEAProject\\test\\spark\\sparkcore\\data\\word*")
		//  2、将每一行数据进行拆分，形成一个一个的单词(扁平化操作，将整体拆分成个体的操作)
		/**
		 * "hello world"  ------>>>  hello, world
		 */
		val words: RDD[String] = lines.flatMap((_: String).split(" "))
		//  3、将数据根据单词进行分组，便于统计
		val wordGroup: RDD[(String, Iterable[String])] =
			words.groupBy((word: String) => word)
		//  4、对分组后的数据进行转换
		/**
		 * (hello, hello, hello)----->>>(hello, 3)
		 */
		val result: RDD[(String, Int)] = wordGroup.map { case (word, list) => (word, list.size)}
		//  5、将结果打印在控制台
		val tuples: Array[(String, Int)] = result.collect()
		tuples.foreach(println)

		//  关闭连接
		sc.stop()

	}
}
