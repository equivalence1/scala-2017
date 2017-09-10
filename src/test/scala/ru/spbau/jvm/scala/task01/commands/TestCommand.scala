package ru.spbau.jvm.scala.task01.commands

object TestCommand extends AbstractCommand {

  override def getPattern: String = "cmd _ _ _"

  override def getPriority: Int = 0

  override def run(list: List[Double]): Double = {
    val a = list.head
    val b = list.tail.head
    val c = list.tail.tail.head

    (a * a + b * b + c * c) / (a + b + c)
  }

}
