package client

import java.io.DataOutputStream
import java.net.Socket
import java.nio.file.{Files, Paths}

import scala.io.StdIn._

object MainClient extends App {

  val socket = new Socket("localhost", 9090)
  val outputStream = new DataOutputStream(socket.getOutputStream)
  new Thread(new ClientThread(socket)).start()

  printFTP()

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
      socket.getOutputStream.write((line + "\n").getBytes(), 0, (line + "\n").length)
    }
  }

  def printFTP(): Unit = {
    print("ftp> ")
  }

  def sendCommand(line: String): Unit = {
    val msg = line.split(" ").head + "\n" + line.split(" ").tail.head
    outputStream.write(msg.getBytes(), 0, msg.length)
    outputStream.flush()
  }

  def sendFile(line: String): Unit = {


    val filePath = line.split(" ").tail.head
    val file = Files.readAllBytes(Paths.get(filePath))
    val header = s"put\nFILE ${Paths.get(filePath).getFileName} ${file.length}\n"

    println(header)

    socket.getOutputStream.write(header.getBytes, 0, header.length)
    socket.getOutputStream.flush()

    socket.getOutputStream.write(file)
    socket.getOutputStream.flush()
  }
}
