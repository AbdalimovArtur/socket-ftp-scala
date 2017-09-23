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
    val msg = "200: DIR Command complete successful."
    socket.getOutputStream.write(msg.getBytes(), 0, msg.length)
    socket.getOutputStream.write(output.getBytes, 0, output.length)
    socket.getOutputStream.flush()
  }

  // Print working directory
  def sendPWD(): Unit = {
    val output = currentWorkingDirectory

    val msg = "200: PWD Command successful.\n"
    socket.getOutputStream.write(msg.getBytes(), 0, msg.length)

    socket.getOutputStream.write(output.getBytes, 0, output.length)
    socket.getOutputStream.flush()
  }

  // Change directory
  def changeDirectory(): Unit = {

    val buffer = new Array[Byte](socket.getInputStream.available())
    socket.getInputStream.read(buffer, 0, socket.getInputStream.available())

    val path = new String(buffer).trim
    var tempWorkingDirectory = currentWorkingDirectory.replaceAll("\\\\", "/")
    println(tempWorkingDirectory)
    println(path)
    path match {
      case ".." => tempWorkingDirectory = tempWorkingDirectory.dropRight(tempWorkingDirectory.length - tempWorkingDirectory.lastIndexOf("/"))
      case _ => tempWorkingDirectory = tempWorkingDirectory + "/" + path
    }

    println(tempWorkingDirectory)
    if (new File(tempWorkingDirectory).isDirectory) {
      currentWorkingDirectory = tempWorkingDirectory
      val msg = "200: CWD Command successful.\n"
      socket.getOutputStream.write(msg.getBytes(), 0, msg.length)
      socket.getOutputStream.flush()
    }
    else {
      val msg = "501: Syntax error in parameters or arguments.\n"
      socket.getOutputStream.write(msg.getBytes(), 0, msg.length)
    }
  }

  // If something goes wrong
  def sendUnknown(): Unit = {
    val msg = "202: Command not implemented, superfluous at this site.\n"
    socket.getOutputStream.write(msg.getBytes(), 0, msg.length)
  }

  def saveFile(): Unit = {

    val header = new String(Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray)

    println(header)
    val fileName = header.split(" ").toList.tail.head
    val fileSize = header.split(" ").toList.tail.tail.head.trim.toInt

    println(fileName)
    println(fileSize)

    val fileOutputStream = new FileOutputStream(s"${System.getProperty("user.home")}\\$fileName")
    var read = 0

    while(socket.getInputStream.available() > 0 && read != fileSize) {

      val available = socket.getInputStream.available()
      val fileBuffer = new Array[Byte](available)
      socket.getInputStream.read(fileBuffer)
      fileOutputStream.write(fileBuffer)
      fileOutputStream.flush()
      read += available
    }
    println(read)
    fileOutputStream.close()

    val responseMessage = "200: PUT command successful.\n"
    socket.getOutputStream.write(responseMessage.getBytes(), 0, responseMessage.length)
  }

  def sendFile(): Unit = {

    val buffer = new Array[Byte](socket.getInputStream.available())
    socket.getInputStream.read(buffer, 0, socket.getInputStream.available())

    val fileName = new String(buffer)
    val pathToFile = currentWorkingDirectory + "/" + fileName
    println(fileName)
    println(pathToFile)

    if (Files.exists(Paths.get(pathToFile))) {
      val file = Files.readAllBytes(Paths.get(pathToFile))
      val header = s"FILE $fileName ${file.size}"
      println(header)
      socket.getOutputStream.write(header.getBytes(), 0, header.length)
      socket.getOutputStream.flush()
      Thread.sleep(1000)
      socket.getOutputStream.write(file, 0, file.length)
      socket.getOutputStream.flush()
    } else {
      val responseMessage = "501: Syntax error in parameters or arguments.\n"
      socket.getOutputStream.write(responseMessage.getBytes(), 0, responseMessage.length)
      socket.getOutputStream.flush()
    }
  }
}
