package controller

import common.TController

/**
  * 控制层
  */
class WordCountController extends TController {

  import service.WordCountService

  private val wordCountService = new WordCountService()

  // 调度
  def dispatch(): Unit = {
    // TODO 执行业务操作
    val array: Array[(String, Int)] = wordCountService.dataAnalysis()
    array.foreach(println)
  }
}
