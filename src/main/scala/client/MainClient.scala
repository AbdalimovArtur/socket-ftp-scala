package client

import java.io.DataOutputStream
import java.net.Socket

import scala.io.StdIn._

object MainClient extends App {

  val socket = new Socket("localhost", 9090)
  val outputStream = new DataOutputStream(socket.getOutputStream)
  new Thread(new ClientThread(socket)).start()

  while(true) {

    val line = readLine()

    if (line.startsWith("cd")) {
      sendMessage(line.split(" ").head + "\n")
      sendMessage(line.split(" ").tail.head)
    } else {
      sendMessage(line + "\n")
    }
  }

  def sendMessage(line: String): Unit = {
    outputStream.write(line.getBytes(), 0, line.length)
    outputStream.flush()
  }
}
