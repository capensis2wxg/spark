package rdd.driverexec

/*
    @author wxg
    @date 2021/7/22-0:35
 */
class DataStru extends Serializable {
  // 数据
  val datas: List[Int] = List(1, 2, 3, 4)
  //  计算逻辑
  val logic: Int => Int = (_: Int) * 2
}
