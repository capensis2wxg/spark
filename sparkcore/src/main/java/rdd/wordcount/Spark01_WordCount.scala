package rdd.wordcount

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/*
    @author wxg
    @date 2021/11/24-11:52
 */
object Spark01_WordCount {
	def main(args: Array[String]): Unit = {
		val sparConf: SparkConf =
			new SparkConf().setMaster("local").setAppName("WordCount")
		val sc = new SparkContext(sparConf)

		// TODO 执行业务操作

		// 1. 读取文件，获取一行一行的数据
		//    hello world
		val lines: RDD[String] =
		sc.textFile("E:\\IDEAProject\\test\\spark\\sparkcore\\data\\word*")

		// 2. 将一行数据进行拆分，形成一个一个的单词（分词）
		//    扁平化：将整体拆分成个体的操作
		//   "hello world" => hello, world, hello, world
		val words: RDD[String] = lines.flatMap((_: String).split(" "))

		// 3. 将单词进行结构的转换,方便统计
		// word => (word, 1)
		val wordToOne: RDD[(String, Int)] = words.map((word: String) => (word, 1))

		// 分组group
		val wordGroup: RDD[(String, Iterable[(String, Int)])] =
			wordToOne.groupBy((t: (String, Int)) => t._1)
		// (Hello,CompactBuffer((Hello,1), (Hello,1), (Hello,1), (Hello,1)))
		wordGroup.foreach(println)

		// 将不同文件的wordCount计数
		val wordCount: RDD[(String, Int)] = wordGroup.map {
			case (_, list) =>
				list.reduce((t1: (String, Int), t2: (String, Int)) => {
					(t1._1, t1._2 + t2._2)
				})
		}
		//  5、将结果打印在控制台
		val tuples: Array[(String, Int)] = wordCount.collect()
		tuples.foreach(println)

		//  关闭连接
		sc.stop()
	}
}
