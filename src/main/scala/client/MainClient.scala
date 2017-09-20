package client

import java.io.DataOutputStream
import java.net.Socket
import java.nio.file.{Files, Paths}

import scala.io.StdIn._

object MainClient extends App {

  val socket = new Socket("localhost", 9090)
  val outputStream = new DataOutputStream(socket.getOutputStream)
  new Thread(new ClientThread(socket)).start()

  while(true) {

    val line = readLine()

    if (line.startsWith("cd")) {
      sendCommand(line)
    } else if (line.startsWith("get")) {
      sendCommand(line)
    } else if (line.startsWith("put")) {
      sendFile(line)
    }
    else {
      sendMessage(line + "\n")
    }
  }

  def sendMessage(line: String): Unit = {
    outputStream.write(line.getBytes(), 0, line.length)
    outputStream.flush()
  }

  def sendCommand(line: String): Unit = {
    sendMessage(line.split(" ").head + "\n")
    sendMessage(line.split(" ").tail.head)
  }

  def sendFile(line: String): Unit = {

    val message = "put" + "\n"
    socket.getOutputStream.write(message.getBytes, 0, message.length)
    socket.getOutputStream.flush()

    val filePath = line.split(" ").tail.head
    val file = Files.readAllBytes(Paths.get(filePath))
    val header = s"FILE ${Paths.get(filePath).getFileName} ${file.length}"
    println(header)
    socket.getOutputStream.write(header.getBytes, 0, header.length)
    socket.getOutputStream.flush()

    socket.getOutputStream.write(file)
    socket.getOutputStream.flush()
  }
}
