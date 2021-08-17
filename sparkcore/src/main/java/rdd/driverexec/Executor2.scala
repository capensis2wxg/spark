package rdd.driverexec
import java.io.{InputStream, ObjectInputStream}
import java.net.{ServerSocket, Socket}
/*
    @author wxg
    @date 2021/7/22-0:39
 */
object Executor2 {
  def main(args: Array[String]): Unit = {
    var socket = new ServerSocket(8888)
    println("服务器启动，等待接收数据")
    //	等待客户端的连接
    val client: Socket = socket.accept();
    val inputStream: InputStream = client.getInputStream
    val objIn = new ObjectInputStream(inputStream)
    val task: Task = objIn.readObject().asInstanceOf[Task]
    val value: List[Int] = task.computer()
    println(" 8888 端口的结果为：" + value)

    objIn.close()
    client.close()
    socket.close()
  }

}
