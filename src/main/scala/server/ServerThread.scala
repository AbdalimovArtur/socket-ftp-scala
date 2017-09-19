package server

import java.io.{DataInputStream, DataOutputStream, File}
import java.net.{Socket, SocketException}

/**
  * Created by Artur on 19.09.2017.
  */
class ServerThread(val socket: Socket) extends Runnable {

  private var currentWorkingDirectory = System.getProperty("user.home")

  override def run(): Unit = {

    try {
      while (socket.isConnected) {
        val received = new String(Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray)
        received.toUpperCase match {
          case "DIR" => sendDirectories()
          case "PWD" => sendPWD()
          case _ => sendUnknown()
        }
        println(received)
      }
    } catch {
      case s: SocketException => println("Socket closed")
    }
    println("End of session")
  }


  def sendDirectories(): Unit = {
    val output = new File(currentWorkingDirectory).listFiles().map(_.getName).toList.mkString("\n")
    socket.getOutputStream.write(output.getBytes, 0, output.length)
    socket.getOutputStream.flush
  }

  def sendPWD(): Unit = {
    val output = currentWorkingDirectory
    socket.getOutputStream.write(output.getBytes, 0, output.length)
    socket.getOutputStream.flush()
  }

  def sendUnknown(): Unit = {

  }
}
