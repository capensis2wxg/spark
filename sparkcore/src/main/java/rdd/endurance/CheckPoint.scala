package rdd.endurance

/*
    @author wxg
    @date 2021/8/4-14:28
 */
object CheckPoint {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
		所谓的检查点其实就是通过将 RDD 中间结果写入磁盘。
		由于血缘依赖过长会造成容错成本过高，这样就不如在中间阶段做检查点容错，如果检查点之后有节点出现问题，可以从检查点开始重做血缘，减少了开销。
	  对 RDD 进行 checkpoint 操作并不会马上被执行，必须执行 Action 操作才能触发。
   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.storage.StorageLevel
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    // 设置检查点路径
    sc.setCheckpointDir("./checkpoint1")
    // 创建一个 RDD，读取指定位置文件
    val lineRdd: RDD[String] = sc.textFile("sparkcore/data/wordCount1")
    // 业务逻辑
    val wordRdd: RDD[String] =
      lineRdd.flatMap((line: String) => line.split(" "))
    val wordToOneRdd: RDD[(String, Long)] = wordRdd.map { word: String =>
      {
        (word, System.currentTimeMillis())
      }
    }
    // 增加缓存,避免再重新跑一个 job 做 checkpoint
    wordToOneRdd.cache()
    // 数据检查点：针对 wordToOneRdd 做检查点计算, 必须落盘存储，文件不会自动删除，通常文件保存在分布式存储系统HFDS
    wordToOneRdd.checkpoint()
    // 触发执行逻辑
    wordToOneRdd.collect().foreach(println)
    sc.stop()
  }

}
