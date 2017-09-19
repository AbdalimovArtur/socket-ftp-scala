package client

import java.io.DataOutputStream
import java.net.Socket

import scala.io.StdIn._


/**
  * Created by Artur on 19.09.2017.
  */
object MainClient extends App {
  val socket = new Socket("localhost", 9090)

  val outputStream = new DataOutputStream(socket.getOutputStream)

  new Thread(new ClientThread(socket)).start()

  while(true) {
    val line = readLine()
    println(s"Sending $line")
    outputStream.write(line.getBytes(), 0, line.length)
    outputStream.flush()
  }
}
