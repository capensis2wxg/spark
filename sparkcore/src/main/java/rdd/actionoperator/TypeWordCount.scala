package rdd.actionoperator

import scala.collection.JavaConverters._

/*
    @author wxg
    @date 2021/8/3-14:13
 */
object TypeWordCount {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    wcGroupBy()
    wcGroupBy()
    wcReduce()
    wcReduceByKey()
    wcAggregateByKey()
    wcFoldByKey()
    wcCombineByKey()
    wcCountByKey()
    wcCountByValue()

    //  groupBy
    def wcGroupBy(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val group: RDD[(String, Iterable[String])] =
        word.groupBy((word: String) => word)
      val wordCount: RDD[(String, Int)] =
        group.mapValues((iter: Iterable[String]) => iter.size)
      wordCount.collect().foreach(println)
      println("=========================================")
    }

    //  groupByKey
    def wcGroupByKey(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordPlusOne: RDD[(String, Int)] = word.map((_, 1))
      val group: RDD[(String, Iterable[Int])] = wordPlusOne.groupByKey()
      val wordCount: RDD[(String, Int)] =
        group.mapValues((iter: Iterable[Int]) => iter.size)
      wordCount.collect().foreach(println)
      println("=========================================")
    }

    //  rdd.reduceByKey
    def wcReduceByKey(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordPlusOne: RDD[(String, Int)] = word.map((_, 1))
      val wordCount: RDD[(String, Int)] = wordPlusOne.reduceByKey(_ + _)
      wordCount.collect().foreach(println)
      println("=========================================")
    }

    //  rdd.aggregateByKey
    def wcAggregateByKey(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordPlusOne: RDD[(String, Int)] = word.map((_, 1))
      val wordCount: RDD[(String, Int)] =
        wordPlusOne.aggregateByKey(0)(_ + _, _ + _)
      wordCount.collect().foreach(println)
      println("=========================================")
    }

    //  rdd.foldByKey
    def wcFoldByKey(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordPlusOne: RDD[(String, Int)] = word.map((_, 1))
      val wordCount: RDD[(String, Int)] = wordPlusOne.foldByKey(0)(_ + _)
      wordCount.collect().foreach(println)
      println("=========================================")
    }

    //  rdd.combineByKey
    def wcCombineByKey(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordPlusOne: RDD[(String, Int)] = word.map((_, 1))
      val wordCount: RDD[(String, Int)] = wordPlusOne.combineByKey(
        (v: Int) => v,
        (x: Int, y: Int) => x + y,
        (x: Int, y: Int) => x + y
      )
      wordCount.collect().foreach(println)
      println("=========================================")
    }

    //	countByKey
    def wcCountByKey(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordPlusOne: RDD[(String, Int)] = word.map((_, 1))
      val wordCount: collection.Map[String, Long] = wordPlusOne.countByKey()
      println(wordCount.toList)
      println("=========================================")
    }

    //countByValue
    def wcCountByValue(): Unit = {
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordCount: collection.Map[String, Long] = word.countByValue()
      println(wordCount.toList)

    }

    //reduce
    def wcReduce(): Unit = {
      import scala.collection.mutable
      val rdd: RDD[String] = sc.makeRDD(List("hello scala", "hello spark"))
      val word: RDD[String] = rdd.flatMap(_.split(" "))
      val wordMap: RDD[mutable.Map[String, Long]] =
        word.map((word: String) => mutable.Map[String, Long]((word, 1)))
      wordMap.reduce((map1: mutable.Map[String, Long], map2) => {
        map2.foreach {
          case (word, count) => {
            val int: Long = map1.getOrElse(word, 0L) + count
            map1.update(word, int)
          }
        }
        map1
      })
    }

  }
}
