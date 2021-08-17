package rdd.RDDDependencies

/*
    @author wxg
    @date 2021/8/4-11:22
 */
object Dependencies {
  /*
	  RDD 只支持粗粒度转换，即在大量记录上执行的单个操作。将创建 RDD 的一系列 Lineage
  （血统）记录下来，以便恢复丢失的分区。RDD 的 Lineage 会记录 RDD 的元数据信息和转
  换行为，当该 RDD 的部分分区数据丢失时，它可以根据这些信息来重新运算和恢复丢失的
  数据分区。
  	这里所谓的依赖关系，其实就是两个相邻 RDD 之间的关系
  	窄依赖表示每一个父(上游)RDD 的 Partition 最多被子（下游）RDD 的一个 Partition 使用，
窄依赖我们形象的比喻为独生子女。
	宽依赖表示同一个父（上游）RDD 的 Partition 被多个子（下游）RDD 的 Partition 依赖，会
引起 Shuffle，总结：宽依赖我们形象的比喻为多生。
	DAG（Directed Acyclic Graph）有向无环图是由点和线组成的拓扑图形，该图形具有方向，
不会闭环。例如，DAG 记录了 RDD 的转换过程和任务的阶段。
	RDD 任务切分中间分为：Application、Job、Stage 和 Task
		⚫  Application：初始化一个 SparkContext 即生成一个 Application；
		⚫  Job：一个 Action 算子就会生成一个 Job；
		⚫  Stage：Stage 等于宽依赖(ShuffleDependency)的个数加 1；
		⚫  Task：一个 Stage 阶段中，最后一个 RDD 的分区个数就是 Task 的个数。
		注意：Application->Job->Stage->Task 每一层都是 1 对 n 的关系。
   */

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val fileRDD: RDD[String] = sc.textFile("sparkcore/data/wordCount1")
    println(fileRDD.toDebugString)
    println(fileRDD.dependencies)
    println("----------------------")

    val wordRDD: RDD[String] = fileRDD.flatMap((_: String).split(" "))
    println(wordRDD.toDebugString)
    println(wordRDD.dependencies)
    println("----------------------")

    val mapRDD: RDD[(String, Int)] = wordRDD.map(((_: String), 1))
    println(mapRDD.toDebugString)
    println(mapRDD.dependencies)
    println("----------------------")

    val resultRDD: RDD[(String, Int)] = mapRDD.reduceByKey((_: Int) + (_: Int))
    println(resultRDD.toDebugString)
    println(resultRDD.dependencies)

    resultRDD.collect()
  }

}
