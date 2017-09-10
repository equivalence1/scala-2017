package ru.spbau.jvm.scala.task01.commands

object UnaryMinus extends AbstractCommand {

  override def getPattern: String = "-_"

  override def getPriority: Int = 0

  override def run(list: List[Double]): Double = {
    if (list.size != 1)
      throw new IllegalArgumentException(s"Sum command accepts exactly 2 arguments, given ${list.size}")

    -list.head
  }

}
