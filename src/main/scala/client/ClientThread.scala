package client

import java.io.{DataInputStream, DataOutputStream, File, FileOutputStream}
import java.net.Socket
import java.nio.file.{Files, Paths}

/**
  * Created by Artur on 20.09.2017.
  */
class ClientThread(val socket: Socket) extends Runnable {


  val inputStream = new DataInputStream(socket.getInputStream)
  val outputStream = new DataOutputStream(socket.getOutputStream)

  override def run(): Unit = {
    while(true) {
      val buffer = new Array[Byte](1024)
      if (inputStream.available() > 0) {
        inputStream.read(buffer)
        val received = new String(buffer, "utf-8")
        if (received.startsWith("FILE")) {
          println(received)
          val fileOutputStream = new FileOutputStream("C:\\Users\\Artur\\Desktop\\photo.jpg")
          var read = 0
          val size = Integer.getInteger(received.split(" ").tail.tail.head)
          println(size)
          while(inputStream.available() > 0 && read != size) {
            val available = inputStream.available()
            val fileBuffer = new Array[Byte](available)
            inputStream.read(fileBuffer)
            fileOutputStream.write(fileBuffer)
            fileOutputStream.flush()
            read += available
          }
          fileOutputStream.close()
        }
        println(new String(buffer, "utf-8"))
      }
    }
  }
}
