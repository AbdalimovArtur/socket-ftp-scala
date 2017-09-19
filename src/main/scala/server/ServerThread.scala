package server

import java.io.{DataInputStream, DataOutputStream}
import java.net.{Socket, SocketException}

/**
  * Created by Artur on 19.09.2017.
  */
class ServerThread(val socket: Socket) extends Runnable {

  override def run(): Unit = {

    try {
      while (socket.isConnected) {
        val received = new String(Stream.continually(socket.getInputStream.read).takeWhile(_ != '\n').map(_.toByte).toArray)
        println(received)
      }
    } catch {
      case s: SocketException => println("Socket closed")
    }
    println("End of session")
  }
}
