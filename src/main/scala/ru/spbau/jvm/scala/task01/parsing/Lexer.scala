package ru.spbau.jvm.scala.task01.parsing

import java.util

import ru.spbau.jvm.scala.task01.commands.{AbstractCommand, CommandType, CommandsSet}

final class Lexer(commandsSet: CommandsSet, var str: String) {

  private object LexerState extends Enumeration {
    val None, Number, Command, OpenBracket, CloseBracket = Value
  }

  str = "(" + str + ") " // just for convenience
  private[this] var leftBound = 0
  private[this] var rightBound = 0
  private[this] var state = LexerState.None
  private[this] var lastMeaningfulState = LexerState.None
  private[this] val lexemes: util.ArrayList[Lexeme] = new util.ArrayList[Lexeme]

  def run: util.List[Lexeme] = {
    while (!isEndOfString) {
      nextLexeme()
    }
    lexemes
  }

  private[this] def isEndOfString: Boolean = {
    rightBound >= str.length
  }

  private[this] def nextLexeme(): Unit = {
    str.charAt(rightBound) match {
      case ' ' =>
        startNewLexeme(LexerState.None)
      case '(' =>
        startNewLexeme(LexerState.OpenBracket)
      case ')' =>
        startNewLexeme(LexerState.CloseBracket)
      case ch if isNumberFormat(ch) =>
        if (state != LexerState.Number)
          startNewLexeme(LexerState.Number)
      case _ =>
        if (state != LexerState.Command)
          startNewLexeme(LexerState.Command)
    }
    rightBound += 1
  }

  private[this] def startNewLexeme(value: Lexer.this.LexerState.Value): Unit = {
    putLexeme()
    leftBound = rightBound
    if (state != LexerState.None) {
      lastMeaningfulState = state
    }
    state = value
  }

  private[this] def putLexeme(): Unit = {
    val substr = str.substring(leftBound, rightBound)
    state match {
      case LexerState.None =>
      case LexerState.Number =>
        lexemes.add(NumberLexeme(substr.toDouble))
      case LexerState.Command =>
        lexemes.add(CommandLexeme(findCommand(substr)))
      case LexerState.OpenBracket =>
        lexemes.add(OpenBracketLexeme)
      case LexerState.CloseBracket =>
        lexemes.add(CloseBracketLexeme)
    }
  }

  private[this] def findCommand(str: String): AbstractCommand = {
    lastMeaningfulState match {
      case LexerState.Number =>
        commandsSet.findCommand(str, CommandType.Infix)
      case LexerState.CloseBracket =>
        commandsSet.findCommand(str, CommandType.Infix)
      case _ =>
        commandsSet.findCommand(str, CommandType.Prefix)
    }
  }

  private[this] def isNumberFormat(ch: Char): Boolean = {
    (ch >= '0' && ch <= '9') || ch == '.'
  }

}
