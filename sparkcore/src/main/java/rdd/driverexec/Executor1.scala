package rdd.driverexec

/*
    @author wxg
    @date 2021/7/22-0:34
 */
object Executor1 {

  import java.io.{InputStream, ObjectInputStream}
  import java.net.{ServerSocket, Socket}

  def main(args: Array[String]): Unit = {
    var socket = new ServerSocket(9999);
    println("服务器启动，等待接收数据");
    //	等待客户端的连接
    val client: Socket = socket.accept();
    val inputStream: InputStream = client.getInputStream
    val objIn: ObjectInputStream = new ObjectInputStream(inputStream)
    val task: Task = objIn.readObject().asInstanceOf[Task]
    val value: List[Int] = task.computer()
    println(" 9999 端口的结果为：" + value)

    objIn.close()
    client.close()
    socket.close()

  }
}
