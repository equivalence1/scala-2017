package ru.spbau.jvm.scala.task01.commands

object SinCommand extends AbstractCommand {

  override def getPattern: String = "sin _"

  override def getPriority: Int = 0

  override def run(list: List[Double]): Double = {
    if (list.size != 1)
      throw new IllegalArgumentException(s"Sin command accepts exactly 1 argument, given ${list.size}")

    Math.sin(list.head)
  }

}
