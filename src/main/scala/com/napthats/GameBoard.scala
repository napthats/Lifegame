package com.napthats

import akka.actor.Actor
//import akka.pattern.ask
//import akka.util.duration._
//import akka.util.Timeout


sealed abstract class CellType
case class Live() extends CellType
case class Dead() extends CellType

sealed abstract class Msg
case object Show extends Msg


object GameBoard {
  def apply(msg: String) = new GameBoard(msg)

  private type AroundCell = (Option[CellType], Option[CellType], Option[CellType], Option[CellType], Option[CellType], Option[CellType], Option[CellType], Option[CellType])
  private def aroundToList(around: AroundCell): List[Option[CellType]] = {
    val (c1, c2, c3, c4, c5, c6, c7, c8) = around
    List(c1, c2, c3, c4, c5, c6, c7, c8)
  }

  private def updateCell(x: Int, y: Int)(implicit board: List[List[CellType]]): CellType = {
    assert(x >= 0 && x < board.length && y >= 0 && y < board.length)
    val around = (getCell(x-1,y-1), getCell(x,y-1), getCell(x+1,y-1), getCell(x-1,y), getCell(x+1,y), getCell(x-1,y+1), getCell(x,y+1), getCell(x+1,y+1))
    nextCell(around, board(x)(y))
  }
  private def getCell(x: Int, y: Int)(implicit board: List[List[CellType]]): Option[CellType] = {
    if (x < 0 || x >= board.length || y < 0 || y >= board.length) None
    else Some(board(x)(y))
  }
  private def nextCell(around: AroundCell, current: CellType)(implicit board: List[List[CellType]]): CellType = {
    val live_count = aroundToList(around).count(_ == Some(Live()))
    if (live_count == 3) Live()
    else if (live_count == 2) {
      current match {
        case Live() => Live()
        case _ => Dead()
      }
    }
    else Dead()
  }
}


class GameBoard(msg: String) extends Actor {
  def receive = {
    case Show => {
      tick()
      sender ! view
    }
//    case _ => println(msg)
  }

  private def view: List[List[CellType]] = board
  private def tick(): Unit = {
    board =
      List.range(0, board.length).map(
        x => List.range(0, board.length).map(GameBoard.updateCell(x, _)(board))
      )
  }
  private def showCell(c: CellType): String = {
    if (c == Live()) "++"
    else "--"
  }

  private var board: List[List[CellType]] = List(List(Dead(), Dead(), Dead()), List(Live(), Live(), Live()), List(Dead(), Dead(), Dead()))
}
