package Top10

/*
    @author wxg
    @date 2021/8/7-11:08
 */
object spark03_hotCategory_simple2 {

	import org.apache.spark.{SparkConf, SparkContext}

	def main(args: Array[String]): Unit = {
		import org.apache.spark.rdd.RDD
		//  Q: reduceByKey聚合算子需要进行大量的shuffle操作
		val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("hotCategory")
		val sc = new SparkContext(sparkConf)

		//  1、读取原始数据日志
		val actionRDD: RDD[String] = sc.textFile("sparkcore/data/user_visit_action.txt")
		//	2、将数据转换结构（品类ID，（1， 0， 0）），（品类ID，（0， 1， 0）），（品类ID，（0， 0， 1））
		val flatRDD: RDD[(String, (Int, Int, Int))] =
			actionRDD.flatMap((action: String) => {
				val strings: Array[String] = action.split("_")
				if (strings(6) != "-1") {
					//  点击的场合
					List((strings(6), (1, 0, 0)))
				} else if (strings(8) != "null") {
					//  下单的场合
					val ids: Array[String] = strings(8).split(",")
					ids.map((id: String) => (id, (0, 1, 0)))
				} else if (strings(10) != "null") {
					//  支付的场合
					val ids: Array[String] = strings(10).split(",")
					ids.map((id: String) => (id, (0, 0, 1)))
				} else Nil
			})

		//  3、将相同品类ID的数据进行分组聚合（品类ID，（点击数量， 下单数量， 支付数量））
		val analysisRDD: RDD[(String, (Int, Int, Int))] =
			flatRDD.reduceByKey((t1: (Int, Int, Int), t2: (Int, Int, Int)) =>
				(t1._1 + t2._1, t1._2 + t2._2, t1._3 + t2._3)
			)
		//  4、排序
		val retRDD: Array[(String, (Int, Int, Int))] = analysisRDD
				.sortBy((_: (String, (Int, Int, Int)))._2, ascending = false)
				.take(10)

		//  5、将结果打印在控制台上
		retRDD.foreach(println)
	}
}
