package ru.spbau.jvm.scala.task04

import org.scalatest.FunSuite

class MultisetTest extends FunSuite {

  test("object apply/unapplySeq test") {
    assertResult(0) {
      Multiset(1, 2, 3) match {
        case Multiset(1) => -1
        case Nil => -1
        case Multiset(1, 2) => -1
        case Multiset(1, 2, 3) => 0
      }
    }

    assertResult(0) {
      Multiset("1", "2", "3") match {
        case Multiset("1") => -1
        case Nil => -1
        case Multiset("1", "2") => -1
        case Multiset("1", _*) => 0
      }
    }
  }

  test("class apply test") {
    assertResult(true) {
      Multiset("Some", "absolutely", "random", "set")("random")
    }
    assertResult(false) {
      Multiset("Some", "absolutely", "unrandom", "set")("random")
    }
  }

  test("add test") {
    assertResult(5) {
      Nil.+(1).+("5").+(Nil).+(1).+(1).size
    }
  }

  test("or test") {
    assertResult(9) {
      Multiset(1, 2, 3).or(Multiset("1", "2", "3")).or(Multiset(1, 2, 3)).size
    }
  }

  test("and test") {
    assertResult(3) {
      Multiset(1, 2, 3, 3).and(Multiset(8, 4, 3, 9)).size
    }
    assertResult(0) {
      Multiset(1, 2, 3).and(Multiset("1", "2", "3")).size
    }
  }

  test("filter test") {
    assertResult(3) {
      Multiset(1, 2, 3, 4, 5).filter(_ % 2 == 1).size
    }
  }

  test("map test") {
    assertResult(true) {
      Multiset(1, 2, 3).map(_.toString) match {
        case Multiset("1", "2", "3") => true
        case _ => false
      }
    }
  }

  test("filter/map/flatMap test") {
    assertResult(true) {
      Multiset(1, 2, 3).filter(_ != 2)
        .flatMap(x => Multiset(x, x.toString + "'"))
        .map(x => (x, x)) match {
        case Multiset((1, 1), ("1'", "1'"), (3, 3), ("3'", "3'")) => true
        case _ => false
      }
    }
  }

}
