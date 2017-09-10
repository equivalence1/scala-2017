package ru.spbau.jvm.scala.task01.commands

object MulCommand extends AbstractCommand {

  override def getPattern: String = "_*_"

  override def getPriority: Int = -5

  override def run(list: List[Double]): Double = {
    if (list.size != 2)
      throw new IllegalArgumentException(s"Mul command accepts exactly 2 arguments, given ${list.size}")

    val left = list.head
    val right = list.tail.head

    left * right
  }

}
