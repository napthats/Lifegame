package com.napthats

import akka.actor.Actor
//import akka.pattern.ask
import akka.util.duration._
//import akka.util.Timeout


sealed abstract class CellType
case object Live extends CellType
case object Dead extends CellType

sealed abstract class MsgToGameBoard
case object Show extends MsgToGameBoard
case class ChangeCell(x: Int, y: Int) extends MsgToGameBoard
case object Clear extends MsgToGameBoard


//receive: MsgToGameBoard
//send: String
class GameBoard private (msg: String) extends Actor {
  def receive = {
    case m: MsgToGameBoard => m match {
      case Show => {
        val msg: String =
          "60:60:" ++
          board.foldRight("")(
            (line, acc) => line.foldRight("")((cell, acc) => GameBoard.showCell(cell) ++ acc) ++ acc
          )
        sender ! msg
      }
      case ChangeCell(x, y) => {
        board = board.updated(y, board(y).updated(x, 
          if (board(y)(x) == Live) Dead else Live
        ))
      }
      case GameBoard.Tick => {
//        board.foreach{println(); _.foreach{GameBoard.showCell(_)}}
        tick()
      }
      case Clear => {
        board = List().padTo(60, List().padTo(60, Dead))
      }
    }
    //case _ => println(m)
    //throw an exception at the default case
  }

  private def tick(): Unit = {
    board =
      List.range(0, board.length).map(
        x => List.range(0, board.length).map(GameBoard.updateCell(x, _)(board))
      )
  }

  private var board: List[List[CellType]] = List().padTo(60, List().padTo(60, Dead))

  context.system.scheduler.schedule(3 seconds, 0.5 seconds, self, GameBoard.Tick)
}


object GameBoard {
  def apply(msg: String) = new GameBoard(msg)
  private case object Tick extends MsgToGameBoard

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
    val live_count = aroundToList(around).count(_ == Some(Live))
    if (live_count == 3) Live
    else if (live_count == 2) {
      current match {
        case Live => Live
        case _ => Dead
      }
    }
    else Dead
  }
  private def showCell(c: CellType): String = {
    if (c == Live) "+"
    else "-"
  }
}


