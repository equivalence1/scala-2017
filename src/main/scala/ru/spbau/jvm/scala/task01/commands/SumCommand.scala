package ru.spbau.jvm.scala.task01.commands

object SumCommand extends AbstractCommand {

  override def getPattern: String = "_+_"

  override def getPriority: Int = -11

  override def run(list: List[Double]): Double = {
    if (list.size != 2)
      throw new IllegalArgumentException(s"Sum command accepts exactly 2 arguments, given ${list.size}")

    val left = list.head
    val right = list.tail.head

    left + right
  }

}
