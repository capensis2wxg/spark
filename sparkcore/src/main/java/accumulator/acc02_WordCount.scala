package accumulator

/*
    @author wxg
    @date 2021/8/6-11:19
 */
object acc02_WordCount {

	import org.apache.spark.util.AccumulatorV2
	import org.apache.spark.{SparkConf, SparkContext}
	import scala.collection.mutable

	def main(args: Array[String]): Unit = {
		import org.apache.spark.rdd.RDD
		val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("acc")
		val sc = new SparkContext(sparkConf)
		val rdd: RDD[String] = sc.makeRDD(List("hello", "spark", "hello"))
		//	累加器
		//	创建累加器
		val wcAcc = new MyAccumulator()
		//	注册累加器
		sc.register(wcAcc, "WordCountAcc")
		rdd.foreach((word: String) => wcAcc.add(word))
		//	打印累加器的值
		println(wcAcc.value)

	}

	/*
		自定义累加器	WordCount
		1、继承累加器	In:	输入的数据类型String	Out：累加器返回的数据类型mutable.Map[String, Long]
		2、重写方法
	 */
	class MyAccumulator extends AccumulatorV2[String, mutable.Map[String, Long]] {
		var map: mutable.Map[String, Long] = mutable.Map()

		// 累加器是否为初始状态
		override def isZero: Boolean = map.isEmpty

		// 复制累加器
		override def copy(): AccumulatorV2[String, mutable.Map[String, Long]] = new MyAccumulator()

		// 重置累加器
		override def reset(): Unit = {map.clear()}

		// 向累加器中增加数据 (In)
		override def add(word: String): Unit = {
			// 查询 map 中是否存在相同的单词。如果有相同的单词，那么单词的数量加 1； 如果没有相同的单词，那么在 map 中增加这个单词
			map(word) = map.getOrElse(word, 0L) + 1
		}

		// 合并累加器
		override def merge(other: AccumulatorV2[String, mutable.Map[String, Long]]): Unit = {
			val map1: mutable.Map[String, Long] = map
			val map2: mutable.Map[String, Long] = other.value
			// 两个 Map 的合并
			map = map1.foldLeft(map2)(
				(innerMap: mutable.Map[String, Long], kv: (String, Long)) =>
					{
						innerMap(kv._1) = innerMap.getOrElse(kv._1, 0L) + kv._2
						innerMap
					}
			)
		}

		// 返回累加器的结果 （Out）
		override def value: mutable.Map[String, Long] = map
	}

}
