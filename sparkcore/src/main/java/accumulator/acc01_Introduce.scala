package accumulator

/*
    @author wxg
    @date 2021/8/6-10:48
 */
object acc01_Introduce {
  /*
	  累加器用来把 Executor 端变量信息聚合到 Driver 端。在 Driver 程序中定义的变量，在
	Executor 端的每个 Task 都会得到这个变量的一份新的副本， 每个 task 更新这些副本的值后，
	传回 Driver 端进行 merge。
   */

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    import org.apache.spark.util.LongAccumulator
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("acc")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))

    //	获取系统累加器，spark默认就提供了简单数据聚合的累加器
    val sum: LongAccumulator = sc.longAccumulator("sum")
    /*rdd.foreach((num: Int) => {
      // 使用累加器
      sum.add(num)
    })*/

    val mapRDD: RDD[Int] = rdd.map(num => {
      //	使用累加器
      sum.add(num)
      num
    })
    /*
    	获取累加器的值
    	少加：转换算子中调用累加器，如果没有行动算子的话，那么就不会执行
    	多加：转换算子中调用累加器，如果多次使用行动算子的话，会造成累加器多次执行
    	一般情况下，累加器会放置在行动算子之前进行操作

     */
    mapRDD.collect()
    println("sum = " + sum.value)

    sc.stop()
  }

}
