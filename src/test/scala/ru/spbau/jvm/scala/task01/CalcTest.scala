package ru.spbau.jvm.scala.task01

import org.junit.Assert._
import org.junit.Test
import ru.spbau.jvm.scala.task01.commands.{InvalidTestCommand, TestCommand}

class CalcTest {

  val EPS = 1e-6

  @Test def simpleCalcTest(): Unit = {
    val calc = new Calculator

    assertTrue(Math.abs(calc.runOnLine("sin 1 - 2 + 3 + -2 * 2") - (-2.15852901)) < EPS)
    assertTrue(Math.abs(calc.runOnLine("-1 - 2 - 1") + 4) < EPS)
    assertTrue(Math.abs(calc.runOnLine("(sin(1 - 2 * (1 - 1))) + sin(- - -1)")) < EPS)
    assertTrue(Math.abs(calc.runOnLine("sin sin - - 1 + - sin sin - - - - 1")) < EPS)
  }

  @Test def advancedCalcTest(): Unit = {
    val calc = new Calculator
    calc.commandsSet.registerCommand(TestCommand)

    assertTrue(Math.abs(calc.runOnLine("(cmd 1.2 2.23 3.112 * 3 + sin 12) * 2 + -2") - 11.69063649) < EPS)
    assertTrue(Math.abs(calc.runOnLine("(- cmd 1.2 2.23 3.112 * 3 + sin 12) * 2 + -2") + 17.83692816) < EPS)
  }

  @Test(expected = classOf[IllegalStateException]) def invalidCommandTest(): Unit = {
    val calc = new Calculator
    calc.commandsSet.registerCommand(InvalidTestCommand)
  }

}
