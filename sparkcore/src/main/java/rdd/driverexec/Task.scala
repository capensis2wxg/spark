package rdd.driverexec

/*
    @author wxg
    @date 2021/7/22-0:57
 */


/**
 * 这个Task任务存在的目的为了实现具体化DataStru
 */
class Task extends Serializable {
	var datas: List[Int] = _
	var logic: Int => Int = _

	def computer(): List[Int] = {
		datas.map(logic)
	}

}
