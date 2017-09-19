package server

import java.net.ServerSocket

/**
  * Created by Artur on 19.09.2017.
  */
object MainServer extends App {

  val serverSocket: ServerSocket = new ServerSocket(9090)

  while(true) {

    val socket = serverSocket.accept()
    println("New connection at " + socket.toString)
    new Thread(new ServerThread(socket)).start()
  }
}
