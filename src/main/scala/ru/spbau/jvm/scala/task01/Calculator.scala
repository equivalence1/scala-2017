package ru.spbau.jvm.scala.task01

import ru.spbau.jvm.scala.task01.commands._
import ru.spbau.jvm.scala.task01.parsing.Lexer
import ru.spbau.jvm.scala.task01.parsing.Parser

import scala.io.StdIn

final class Calculator {

  val commandsSet = new CommandsSet()

  commandsSet.registerCommand(SumCommand)
  commandsSet.registerCommand(SubtractCommand)
  commandsSet.registerCommand(MulCommand)
  commandsSet.registerCommand(SinCommand)
  commandsSet.registerCommand(UnaryMinus)

  val parser = new Parser

  def run(): Unit = {
    while (true) {
      try {
        val line = StdIn.readLine()
        if (line == null)
          return
        println(s"> ${runOnLine(line)}")
      } catch {
        case e: Exception =>
          println(s"Caught exception\n$e")
      }
    }
  }

  def runOnLine(line: String): Double = {
    val lexer = new Lexer(commandsSet, line)
    val ast = parser.buildAst(lexer.run)
    ast.run()
  }

}
