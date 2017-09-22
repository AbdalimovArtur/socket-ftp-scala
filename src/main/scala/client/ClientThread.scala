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

        // BIKE: Need fixing
        if (received.startsWith("FILE")) {
//          println(received)
          val fileOutputStream = new FileOutputStream(s"C:\\Users\\Artur\\Desktop\\${received.split(" ").tail.head}")
          var alreadyRead = 0
          val receivingFileSize = received.split(" ").tail.tail.head.trim.toInt
          println(s"receiving : $receivingFileSize bytes")

          while(inputStream.available() > 0 || alreadyRead != receivingFileSize) {
            val available = inputStream.available()
            val fileBuffer = new Array[Byte](available)
            inputStream.read(fileBuffer)
            fileOutputStream.write(fileBuffer)
            fileOutputStream.flush()
            alreadyRead += available
          }
          fileOutputStream.close()
        } else {
          println(new String(buffer, "utf-8"))
        }
        // END OF BIKE


        MainClient.printFTP()
      }
    }
  }
}
