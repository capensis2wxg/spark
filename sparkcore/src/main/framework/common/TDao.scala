package common

trait TDao {
  import org.apache.spark.rdd.RDD

  def readFile(path: String): RDD[String] = {
    import util.EnvUtil
    EnvUtil.take().textFile(path)
  }
}
