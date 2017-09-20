package server

import java.io.{DataInputStream, DataOutputStream, File, FileOutputStream}
import java.net.{Socket, SocketException}
import java.nio.file.{Files, Paths}

/**
  * Created by Artur on 19.09.2017.
  */
class ServerThread(val socket: Socket) extends Runnable {

  private var currentWorkingDirectory = System.getProperty("user.home").replaceAll("\\\\", "/")


  override def run(): Unit = {

    try {
      while (socket.isConnected) {
        val received = new String(Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray)
        println(received)
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

  def saveFile(): Unit = {

    val buffer = new Array[Byte](socket.getInputStream.available())
    socket.getInputStream.read(buffer, 0, socket.getInputStream.available())

    val header = new String(buffer)
    println(header)
    val fileName = header.split(" ").tail.head
    val fileSize = Integer.getInteger(header.split(" ").tail.tail.head)


    val fileOutputStream = new FileOutputStream(s"${System.getProperty("user.home")}\\$fileName")
    var read = 0
    println(fileSize)

    while(socket.getInputStream.available() > 0 && read != fileSize) {
      val available = socket.getInputStream.available()
      val fileBuffer = new Array[Byte](available)
      socket.getInputStream.read(fileBuffer)
      fileOutputStream.write(fileBuffer)
      fileOutputStream.flush()
      read += available
    }
    fileOutputStream.close()
  }

  def sendFile(): Unit = {

    val buffer = new Array[Byte](socket.getInputStream.available())
    socket.getInputStream.read(buffer, 0, socket.getInputStream.available())

    val fileName = new String(buffer)
    val pathToFile = currentWorkingDirectory + "/" + fileName
    println(fileName)
    println(pathToFile)
    val file = Files.readAllBytes(Paths.get(pathToFile))
    val header = s"FILE $fileName ${file.size}"
    socket.getOutputStream.write(header.getBytes(), 0, header.length)
    socket.getOutputStream.flush()
    socket.getOutputStream.write(file, 0, file.length)
  }

  def removeFile(): Unit = ???
}
