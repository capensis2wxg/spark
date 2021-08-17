package rdd.actionoperator

/*
    @author wxg
    @date 2021/8/3-11:19
 */
object ActionOperator {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    //	行动算子
    /*
			reduce
				def reduce(f: (T, T)) : T
				函数说明：聚集RDD中的所有元素，先聚集分区内的数据，在集集分区间的数据
     */
    //    val ret: Int = rdd.reduce((_: Int) + (_: Int))
    //    println(ret)

    /*
			collect
				def collect(): Array[]
				函数说明
					在驱动程序中，以数组Array的形式返回数据集的所有元素
     */
    //    val ints: Array[Int] = rdd.collect()
    //    println(ints.mkString(","))

    /*
			count
				def count(): Long
				函数说明：返回RDD中元素的个数
     */
    val count: Long = rdd.count()
    println(count)

    /*
			first
				def first(): T
				函数说明：返回RDD中的第一个元素
     */
    val first: Int = rdd.first()
    println(first)

    /*
			take
				def take(num: Int): Array[T]
				函数说明：返回一个由RDD的前n个元素组成的数组
     */
    val take: Array[Int] = rdd.take(2)
    println(take.mkString("Array(", ", ", ")"))

    /*
			takeOrdered
				def takeOrdered(num: Int)(implicit ord: Ordering[T]): Array[T]
				函数说明：返回该RDD排序后的前n个元素组成的数组
     */
    val value: RDD[Int] = sc.makeRDD(List(4, 5, 2, 3))
    val ints: Array[Int] = value.takeOrdered(3)(Ordering.Int.reverse)
    println(ints.mkString(","))

    /*
			aggregate
			  函数签名 def aggregate[U: ClassTag](zeroValue: U)(seqOp:(U,T)=>U,combOp:():U):U
			  函数说明 分区内的数据通过初始值和分区的数据进行聚合，然后再和初始值进行分区间的数据聚合，而aggregateByKey不需要将
					   初始值再和分区的数据进行聚合

     */
    val aggregate: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
    val sum: Int =
      aggregate.aggregate(10)((_: Int) + (_: Int), (_: Int) + (_: Int))
    println(sum)

    /*
			fold
				def fold(zeroValue: T)(op: (T,T)=>T):T
			函数说明 折叠操作，aggregate的简化版操作
     */
    val fold: Int = aggregate.fold(10)((_: Int) + (_: Int))
    println(fold)

    /*
		countByKey
			函数签名：def countByKey():Map[K, Long]
			函数说明：统计每种key的个数
     */
    val intToLong: collection.Map[Int, Long] = rdd.countByValue()
    println(intToLong)

    val countByCount: RDD[(String, Int)] = sc.makeRDD(
      List(
        ("a", 1),
        ("a", 3),
        ("b", 2)
      )
    )
    val stringToLong: collection.Map[String, Long] = countByCount.countByKey()
    println(stringToLong)

    /*
		save
     */
    rdd.saveAsTextFile("saveText")
    rdd.saveAsObjectFile("saveObjText")
    //  saveAsSequenceFile的数据类型必须为k-v
    val value1: RDD[(String, Int)] =
      sc.makeRDD(List(("a", 1), ("b", 2), ("a", 2)))
    value1.saveAsSequenceFile("saveSeq")

    //  foreach其实是Driver端内存集合的循环遍历方法
    rdd.collect().foreach(println)
    println("===" * 20)
    //  foreach其实是Executor端内存数据打印
    rdd.foreach(println)
    /*
		算子（operator）
			RDD的方法和Scala集合对象的方法不一样
			集合对象的方法都是在同一个节点的内存中完成的
			RDD的方法可以将计算逻辑发送到Executor端（分布式节点）执行
		因此，为了区分不同的效果，将RDD的方法称之为算子
		RDD的方法外部的操作都是在Driver端执行，而方法内部的逻辑操作都是在Executor端执行
     */

    //示例：

    sc.stop()
  }

}
