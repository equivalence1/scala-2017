package ru.spbau.jvm.scala.task01.parsing

import java.util

import ru.spbau.jvm.scala.task01.commands.CommandType

final class Parser {

  /**
    * Build AST of a lexemes list
    * @param lexemes output of lexer
    * @return ast built using given lexemes list
    */
  def buildAst(lexemes: util.List[Lexeme]): AstTree = {
    buildAstOnRange(lexemes, 0, lexemes.size())
  }

  private[this] def buildAstOnRange(lexemes: util.List[Lexeme], l: Int, r: Int): AstTree = {
    /* if only 1 element left it should be a number */
    if (l + 1 == r) {
      lexemes.get(l) match {
        case NumberLexeme(n) =>
          return ValueNode(n)
        case _ =>
          throw new RuntimeException(s"lexeme ${lexemes.get(l)} was expected to be a number")
      }
    }

    /* delete surrounding brackets if we have ones */
    val (_, checkR) = getNextExpressionRange(lexemes, l)
    if (checkR == r && lexemes.get(l) == OpenBracketLexeme)
      return buildAstOnRange(lexemes, l + 1, r - 1)

    /* find command with lowest priority and use it as root of AST */
    val nextCommandId = findLowestPriorityOnRange(lexemes, l, r)
    val cmd = lexemes.get(nextCommandId).asInstanceOf[CommandLexeme].cmd

    if (nextCommandId == l) { /* cmd is a prefix command */
      var args: scala.List[AstTree] = Nil
      var i = l + 1

      while (args.size < cmd.getParamNum) {
        val (eLeft, eRight) = getNextExpressionRange(lexemes, i)
        args = args.::(buildAstOnRange(lexemes, eLeft, eRight))
        i = eRight
      }

      CommandNode(cmd, args)
    } else { /* cmd is an infix command */
      val leftAst = buildAstOnRange(lexemes, l, nextCommandId)
      val rightAst = buildAstOnRange(lexemes, nextCommandId + 1, r)
      CommandNode(cmd, leftAst::rightAst::Nil)
    }
  }

  private[this] def getNextExpressionRange(lexemes: util.List[Lexeme], l: Int): (Int, Int) = {
    var needExpressions = 1
    var balance = 0
    var r = l

    while ((balance != 0 || needExpressions > 0) && r < lexemes.size()) {
      balance += balanceDiff(lexemes.get(r))
      /* this expressions "belong" to nested unary operators */
      if (balance == 0) {
        lexemes.get(r) match {
          case CommandLexeme(cmd) =>
            cmd.getCommandType match {
              case CommandType.Prefix =>
                needExpressions += cmd.getParamNum - 1
            }
          case _ =>
            needExpressions -= 1
        }
      }
      r += 1
    }

    if (balance != 0)
      throw new RuntimeException("bad brackets sequence")

    (l, r)
  }

  private[this] def findLowestPriorityOnRange(lexemes: util.List[Lexeme], l: Int, r: Int): Int = {
    var balance = 0
    var i = l
    var ans = i
    var min = Int.MaxValue

    while (i < r) {
      balance += balanceDiff(lexemes.get(i))
      if (balance == 0) {
        lexemes.get(i) match {
          case CommandLexeme(cmd) =>
            if (min == cmd.getPriority && cmd.getCommandType == CommandType.Infix) {
              ans = i
            }
            if (min > cmd.getPriority) {
              ans = i
              min = cmd.getPriority
            }
          case _ =>
        }
      }
      i += 1
    }

    ans
  }

  private[this] def balanceDiff(lexeme: Lexeme): Int = {
    if (lexeme == OpenBracketLexeme)
      return 1
    if (lexeme == CloseBracketLexeme)
      return -1
    0
  }

}
