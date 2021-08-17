package rdd.DifferenceBetweenAggregationOperators

/*
    @author wxg
    @date 2021/8/1-11:12
 */
object DifferenceBetweenAggregation {

  import org.apache.spark.{SparkConf, SparkContext}

  /*
	rdd.reduceByKey: 相同 key 的第一个数据不进行任何计算，分区内和分区间计算规则相同
		 combineByKeyWithClassTag[V]((v: V) => v, func, func, rdd.partitioner)
	FoldByKey: 相同 key 的第一个数据和初始值进行分区内计算，分区内和分区间计算规则相同
		combineByKeyWithClassTag[V]((v: V) => cleanedFunc(createZero(), v), cleanedFunc, cleanedFunc, rdd.partitioner)
	AggregateByKey： 相同 key 的第一个数据和初始值进行分区内计算，分区内和分区间计算规则可以不相同
		combineByKeyWithClassTag[U]((v: V) => cleanedSeqOp(createZero(), v),cleanedSeqOp, combOp, rdd.partitioner)
	CombineByKey:当计算时，发现数据结构不满足要求时，可以让第一个数据转换结构。分区内和分区间计算规则不相同。
		 combineByKeyWithClassTag(createCombiner, mergeValue, mergeCombiners,rdd.partitioner, mapSideCombine, serializer)(null)

   */
  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)

    val rdd: RDD[(String, Int)] =
      sc.makeRDD(
        List(("a", 2), ("a", 2), ("b", 3), ("b", 4), ("b", 5), ("b", 6))
      )

    //  计算wordCount的四种方法
    rdd.reduceByKey(_ + _)
    rdd.aggregateByKey(0)(_ + _, _ + _)
    rdd.foldByKey(0)(_ + _)
    rdd.combineByKey(
      v => v,
      (x: Int, y: Int) => x + y,
      (x: Int, y: Int) => x + y
    )
  }

}
