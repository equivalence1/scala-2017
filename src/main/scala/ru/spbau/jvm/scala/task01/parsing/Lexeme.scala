package ru.spbau.jvm.scala.task01.parsing

import ru.spbau.jvm.scala.task01.commands.AbstractCommand

abstract class Lexeme
case object OpenBracketLexeme extends Lexeme
case object CloseBracketLexeme extends Lexeme
case class NumberLexeme(n: Double) extends Lexeme
case class CommandLexeme(cmd: AbstractCommand) extends Lexeme
