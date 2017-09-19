package server

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket

/**
  * Created by Artur on 19.09.2017.
  */
class ServerThread(val socket: Socket) extends Runnable {

  val inputStream = new DataInputStream(socket.getInputStream())
  val outputStream = new DataOutputStream(socket.getOutputStream())

  override def run(): Unit = {
    println("Running")
    while(true) {
      val buffer = new Array[Byte](1024)
      val string = new StringBuilder()
      println("Start reading from inputStream")
      inputStream.read(buffer)
      println(s"Message ${new String(buffer, "utf-8")}")
//      Stream.continually(inputStream.read(buffer)).takeWhile(_ != -1).foreach(string.append(new String(buffer)))
      println("Read String")
      if (!string.isEmpty) {
        println(string.toString())
      }
    }
  }
}
