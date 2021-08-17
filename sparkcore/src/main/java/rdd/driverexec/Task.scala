package rdd.driverexec

/*
    @author wxg
    @date 2021/7/22-0:57
 */
class Task extends Serializable {
  var datas: List[Int] = _
  var logic: (Int) => Int = _

  def computer(): List[Int] = {
    datas.map(logic)
  }

}
