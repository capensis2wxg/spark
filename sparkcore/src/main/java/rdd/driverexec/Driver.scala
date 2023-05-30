package rdd.driverexec
import java.net.Socket
import java.io.{ObjectOutputStream, OutputStream}
/*
    @author wxg
    @date 2021/7/22-0:34
 */
object Driver {
	def main(args: Array[String]): Unit = {
		val client1: Socket = new Socket("localhost", 1111)
		val client2: Socket = new Socket("localhost", 2222)

		val dataStru: DataStru = new DataStru()
		val task: Task = new Task()

		//	客户端1
		val stream1: OutputStream = client1.getOutputStream
		val objOut1: ObjectOutputStream = new ObjectOutputStream(stream1)
		task.logic = dataStru.logic
		task.datas = dataStru.datas.take(2)
		objOut1.writeObject(task)
		objOut1.flush()
		objOut1.close()
		client1.close()

		//	客户端2
		val stream2: OutputStream = client2.getOutputStream
		val objOut2: ObjectOutputStream = new ObjectOutputStream(stream2)
		task.logic = dataStru.logic
		task.datas = dataStru.datas.takeRight(2)
		objOut2.writeObject(task)
		objOut2.flush()
		objOut2.close()
		client2.close()

		println("客户端数据发送完毕")

	}
}
