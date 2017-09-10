package ru.spbau.jvm.scala.task01.commands

object InvalidTestCommand extends AbstractCommand {

  override def getPattern: String = "_ blah-blah _"

  override def getPriority: Int = 0

  override def run(list: List[Double]): Double = 0

}
