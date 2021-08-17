package rdd.Serialization

/*
    @author wxg
    @date 2021/8/4-9:00
 */
object ClosureInspect {

  import org.apache.spark.{SparkConf, SparkContext}

  def main(args: Array[String]): Unit = {
    import org.apache.spark.rdd.RDD
    val sparkConf: SparkConf =
      new SparkConf().setMaster("local[*]").setAppName("rdd/map")
    val sc = new SparkContext(sparkConf)
    //val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4))
    val rdd: RDD[Int] = sc.makeRDD(List[Int]())
    //	Driver端
    val user = new User()
    //	Caused by: java.io.NotSerializableException: rdd.Serialization.ClosureInspect$User
    /*
    	Executor
    	rdd算子方法中的操作会进行闭包检测，判断算子外的操作是否可以被序列化
     */
    rdd.foreach((num: Int) => {
      println("age = " + (num + user.age))
      println("----" * 10)
    })

    sc.stop()

  }
//  class User extends Serializable {
//    val age: Int = 20;
//  }

  //	样例类会自动混入序列化特质
  case class User() {
    val age: Int = 20;
  }

//  class User {
//    val age: Int = 20;
//  }
}
