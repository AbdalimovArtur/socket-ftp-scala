package client

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket

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
        println(new String(buffer, "utf-8"))
      }
    }
  }
}
