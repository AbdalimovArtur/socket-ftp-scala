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
          case "CD" => changeDirectory()
          case "GET" => sendFile()
          case "PUT" => saveFile()
          case "RM" => removeFile()
          case _ => sendUnknown()
        }
      }
    } catch {
      case s: SocketException => {
        socket.close()
        println("Socket closed")
      }
    }
    println("End of session")
  }


  // Directories
  def sendDirectories(): Unit = {
    val output = new File(currentWorkingDirectory).listFiles().map(_.getName).toList.mkString("\n")
    socket.getOutputStream.write(output.getBytes, 0, output.length)
    socket.getOutputStream.flush()
  }

  // Print working directory
  def sendPWD(): Unit = {
    val output = currentWorkingDirectory
    socket.getOutputStream.write(output.getBytes, 0, output.length)
    socket.getOutputStream.flush()
  }

  // Change directory
  def changeDirectory(): Unit = {

    val buffer = new Array[Byte](socket.getInputStream.available())
    socket.getInputStream.read(buffer, 0, socket.getInputStream.available())

    val path = new String(buffer)
    var tempWorkingDirectory = currentWorkingDirectory.replaceAll("\\\\", "/")

    path match {
      case ".." => tempWorkingDirectory = tempWorkingDirectory.dropRight(tempWorkingDirectory.length - tempWorkingDirectory.lastIndexOf("/"))
      case _ => tempWorkingDirectory += "/" + path.takeWhile(x => x.isLetter)
    }

    if (new File(tempWorkingDirectory).isDirectory) currentWorkingDirectory = tempWorkingDirectory
    else {
      val msg = "Error, directory doesn't exists"
      socket.getOutputStream.write(msg.getBytes(), 0, msg.length)
    }
  }

  // If something goes wrong
  def sendUnknown(): Unit = {
    val msg = "Unknown command, try again"
    socket.getOutputStream.write(msg.getBytes(), 0, msg.length)
  }
  
  def saveFile(): Unit = ???

  def sendFile(): Unit = ???

  def removeFile(): Unit = ???
}
