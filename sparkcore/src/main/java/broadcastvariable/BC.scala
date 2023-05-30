package broadcastvariable

/*
    @author wxg
    @date 2021/8/6-12:02
 */
object BC {

	import org.apache.spark.{SparkConf, SparkContext}

	/*
		闭包数据都是以Task为单位发送的，每个任务中包含闭包数据。这样，可能导致一个Executor中包含大量的重复数据，并且占用大量的重复内存。
	Executor其实就是一个JVM，所以在启动时会自动分配内存。因此，完全可以将任务中的闭包数据放置在Executor的内存中，达到共享的目的。
	Spark中的广播变量就可以将闭包中的数据保存到Executor的内存中。注意：Spark中的广播变量不能够更改，因此才被称为分布式共享只读变量。
		广播变量用来高效分发较大的对象。 向所有工作节点发送一个较大的只读值， 以供一个或多个 Spark 操作使用。
	比如，如果你的应用需要向所有节点发送一个较大的只读查询表，广播变量用起来都很顺手。在多个并行操作中使用同一个变量，
	但是 Spark 会为每个任务分别发送。

	 */
	def main(args: Array[String]): Unit = {
		import org.apache.spark.broadcast.Broadcast
		import org.apache.spark.rdd.RDD

		import scala.collection.mutable
		val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("acc")
		val sc = new SparkContext(sparkConf)

		val rdd1: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("b", 1), ("c", 1)))
		val map: mutable.Map[String, Int] = mutable.Map(("a", 2), ("b", 1), ("c", 2))

		//	创建广播变量
		val bc: Broadcast[mutable.Map[String, Int]] = sc.broadcast(map)

		//这里不推荐使用Join，因为join会导致数据量剧增，并且会影响shuffle的性能，不推荐使用

		rdd1
				.map {
					case (word, count) =>
						val I: Int = bc.value.getOrElse(word, 0)
						(word, (count, I))
				}
				.collect()
				.foreach(println)
	}
}
