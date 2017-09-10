package ru.spbau.jvm.scala.task01.parsing

import ru.spbau.jvm.scala.task01.commands.AbstractCommand

abstract class AstTree {
  def run(): Double
}

case class ValueNode(v: Double) extends AstTree {
  override def run(): Double = v
}

case class CommandNode(c: AbstractCommand, children: List[AstTree]) extends AstTree {
  override def run(): Double = {
    c.run(children.map((child) => child.run()))
  }
}
