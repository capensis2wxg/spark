package service

import common.TService
import org.apache.spark.rdd.RDD

/**
  * 服务层
  */
class WordCountService extends TService {

  import dao.WordCountDao

  private val wordCountDao = new WordCountDao()

  // 数据分析
  def dataAnalysis(): Array[(String, Int)] = {
    val lines: RDD[String] = wordCountDao.readFile("sparkcore/data/wordCount1")
    val words: RDD[String] = lines.flatMap((_: String).split(" "))
    val wordToOne: RDD[(String, Int)] = words.map((word: String) => (word, 1))
    val wordToSum: RDD[(String, Int)] =
      wordToOne.reduceByKey((_: Int) + (_: Int))
    val array: Array[(String, Int)] = wordToSum.collect()
    array
  }
}
