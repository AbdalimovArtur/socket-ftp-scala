package client

import java.io.DataOutputStream
import java.net.Socket

import scala.io.StdIn._

object MainClient extends App {

  val socket = new Socket("localhost", 9090)
  val outputStream = new DataOutputStream(socket.getOutputStream)
  new Thread(new ClientThread(socket)).start()

  while(true) {
    var line = readLine()
    if (line.startsWith("cd")) {
      val command = line.split(" ").head + "\n"
      val path = line.split(" ").tail.head
      outputStream.write(command.getBytes(), 0, command.length)
      outputStream.flush()
      outputStream.write(path.getBytes(), 0, path.length)
      outputStream.flush()
    } else {
      println(s"Sending $line")
      line += "\n"
      outputStream.write(line.getBytes(), 0, line.length)
      outputStream.flush()
    }
  }
}
