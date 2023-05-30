package common

import org.apache.spark.{SparkConf, SparkContext}

trait TApplication {

  def start(master: String = "local[*]", app: String = "Application")(
      op: => Unit
  ): Unit = {
    import util.EnvUtil
    val sparConf: SparkConf = new SparkConf().setMaster(master).setAppName(app)
    val sc = new SparkContext(sparConf)
    EnvUtil.put(sc)

    try {
      op
    } catch {
      case ex: Throwable => println(ex.getMessage)
    }

    // TODO 关闭连接
    sc.stop()
    EnvUtil.clear()
  }
}
