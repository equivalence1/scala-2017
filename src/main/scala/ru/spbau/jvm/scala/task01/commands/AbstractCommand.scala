package ru.spbau.jvm.scala.task01.commands

trait AbstractCommand {

  /**
    * Pattern of a command should be an exactly one keyword (might be a symbol) and
    * positions of its arguments represented by '_' character
    *
    * More formally, Pattern := `_ @ _ | @ _+`, where '@' is a command name
    *
    * So command is either a binary operand or a function with arbitrary number of parameters
    *
    * @return pattern of this command
    */
  def getPattern: String

  /**
    * The priority of this command. The higher priority is, the lower it will be presented in an AST
    *
    * e.g. * has a higher priority than +, so expression like 1 + 2 * 3 is presented like this:
    *
    *     +
    *    / \
    *   1  *
    *     /\
    *    2 3
    *
    * And the result is 7
    *
    * @return priority of this operation
    */
  def getPriority: Int

  /**
    * run this command
    * @param list arguments of this command
    * @return result of its execution
    */
  def run(list: List[Double]): Double

  final def getName: String = {
    getPattern.filterNot(" _".toSet)
  }

  final def getParamNum: Int = {
    getPattern.count(_ == '_')
  }

  final def getCommandType: CommandType.Value = {
    getPattern.filterNot(" ".toSet).charAt(0) match {
      case '_' =>
        CommandType.Infix
      case _ =>
        CommandType.Prefix
    }
  }

  val NAME_PATTERN = "[^\\s\\._()]+"

  final def checkCommand(): Unit = {
    if (!getPattern.filterNot(" ".toSet).matches(s"(_${NAME_PATTERN}_|$NAME_PATTERN(_)+)"))
      throw new IllegalStateException(s"command $getPattern has invalid pattern")

    if (getParamNum == 0)
      throw new IllegalStateException(s"command $getPattern has no arguments")

    /* prefix commands should always has higher priority than infix ones */

    if (getPriority >= 0 && getCommandType == CommandType.Infix)
      throw new IllegalStateException(s"command $getPattern is infix but has priority >= 0")

    if (getPriority != 0 && getCommandType == CommandType.Prefix)
      throw new IllegalStateException(s"command $getPattern is prefix but has priority != 0")
  }

}