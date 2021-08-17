package rdd.driverexec

/*
    @author wxg
    @date 2021/7/22-0:35
 */
class DataStru extends Serializable {
  val datas: List[Int] = List(1, 2, 3, 4)
  val logic: Int => Int = _ * 2
}
