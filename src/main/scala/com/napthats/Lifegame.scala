package com.napthats


import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.websocket.WebSocket
import org.eclipse.jetty.websocket.WebSocket.Connection
import org.eclipse.jetty.websocket.WebSocketServlet
import javax.servlet.http.HttpServletRequest
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout
import akka.dispatch.Await


class WebSocketBridge(board: ActorRef) extends WebSocket.OnTextMessage {
  implicit val timeout = Timeout(5 seconds)

  def onOpen(con: Connection) {
  }

  def onClose(arg0: Int, arg1: String) {

  }

  def onMessage(msg: String) {
    val view_future = board ? Show
    println(Await.result(view_future, timeout.duration))
  }

  def disconnectServer() {

  }


  def disconnectClient() {

  }

  def connectServer(host: String, port: Int) {

  }

  def send(msg: String) {

  }
}


object LifeGame {
  def main(args: Array[String]) {
    println(Live())
    println(Live)
    if(Live() == Live) println("yes")
    else println("no")

    val actor_system = ActorSystem()
    val board = actor_system.actorOf(Props(GameBoard("hi")), name = "test")

/*    GameBoard.view.foreach(
      line => {
        println("")
        line.foreach(c => print(GameBoard.showCell(c)))
      }
    )
    GameBoard.tick()
    GameBoard.view.foreach(
      line => {
        println("")
        line.foreach(c => print(GameBoard.showCell(c)))
      }
    )
    GameBoard.tick()
    GameBoard.view.foreach(
      line => {
        println("")
        line.foreach(c => print(GameBoard.showCell(c)))
      }
    )
    GameBoard.tick()
    GameBoard.view.foreach(
      line => {
        println("")
        line.foreach(c => print(GameBoard.showCell(c)))
      }
    )*/

    /*
        //GameBoard.view(0)(0) = Live()
        if(GameBoard.view(1)(0) == Dead()) {
          println("yes")
        }
        else {
          println("no")
        }
        GameBoard.tick()
        if(GameBoard.view(1)(0) == Dead()) {
          println("yes")
        }
        else {
          println("no")
        }
        GameBoard.tick()
        if(GameBoard.view(1)(0) == Dead()) {
          println("yes")
        }
        else {
          println("no")
        }
    */

//    return

    val server = new Server(8080)
    server.setStopAtShutdown(true)
    server.setGracefulShutdown(1000)
    val root = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS)
    root.setResourceBase("./")
    //root.addServlet(DefaultServlet.class, "/*")
    root.addServlet(new ServletHolder(new WebSocketServlet() {
      val serialVersionUID = 1

      def doWebSocketConnect(y: HttpServletRequest, z: String): WebSocket = {
        new WebSocketBridge(board)
      }
    }), "/ws/*")
    server.start()
  }
}
