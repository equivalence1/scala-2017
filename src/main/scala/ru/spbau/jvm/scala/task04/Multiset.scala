package ru.spbau.jvm.scala.task04


/**
  * immutable Multiset implementation.
  *
  * I think that Set should be covariant {{{(Set[Cat] <: Set[Animal])}}} thus this strange implementation of apply
  *
  * Also I found it cheating to use scala's std (e.g. implement my multiset using just List, so that set.map is actually
  * just new Multiset(this.inner-list.map)). I implement set as a stack just cuz it's easier to implement it so
  *
  */
abstract class Multiset[+A] {

  def top: A
  def bot: Multiset[A]

  def +[B >: A](elem: B): Multiset[B] = new Multiset[B] {
    override def top: B = elem
    override def bot: Multiset[B] = Multiset.this
  }

  /*
   Can not implement normal `apply` here because Multiset is covariant
   */
  def contains[B >: A](elem: B): Boolean = {
    if (top.equals(elem))
      return true
    bot.contains(elem)
  }

  def find[B >: A](f: B => Boolean): Option[A] = {
    if (f(top))
      return Some(top)
    bot.find(f)
  }

  def or[B >: A](set: Multiset[B]): Multiset[B] = new Multiset[B] {
    override def top: B = Multiset.this.top
    override def bot: Multiset[B] = Multiset.this.bot.or(set)
  }

  def and[B >: A](set: Multiset[B]): Multiset[B] = {
    def innerAnd[C, D >: C](set1: Multiset[C], set2: Multiset[D]): Multiset[D] = {
      if (set2 == Nil)
        return Nil
      if (set1.contains(set2.top)) {
        new Multiset[D] {
          override def top: D = set2.top
          override def bot: Multiset[D] = innerAnd(set1, set2.bot)
        }
      } else {
        innerAnd(set1, set2.bot)
      }
    }
    innerAnd(this, set).or(innerAnd(set, this)) // (1, 2) && (1, 3) = (1, 1)
  }

  def filter(f: A => Boolean): Multiset[A] = {
    if (f(top)) {
      new Multiset[A] {
        override def top: A = Multiset.this.top
        override def bot: Multiset[A] = Multiset.this.bot.filter(f)
      }
    } else {
      bot.filter(f)
    }
  }

  def map[B](f: A => B): Multiset[B] = new Multiset[B] {
    override def top: B = f(Multiset.this.top)
    override def bot: Multiset[B] = Multiset.this.bot.map(f)
  }

  def flatMap[B](f: A => Multiset[B]): Multiset[B] = {
    f(top).or(bot.flatMap(f))
  }

  def toSeq: Seq[A] = {
    List(top).++(bot.toSeq)
  }

  def size: Int = toSeq.size

  override def toString: String = {
    top.toString + " :: " + bot.toString
  }

}

object Nil extends Multiset[Nothing] {
  override def top: Nothing = throw new NoSuchElementException("Nil doesn't have top")
  override def bot: Nothing = throw new NoSuchElementException("Nil doesn't have bot")

  override def contains[B >: Nothing](elem: B): Boolean = false
  override def find[B >: Nothing](f: B => Boolean): Option[Nothing] = None
  override def or[B >: Nothing](set: Multiset[B]): Multiset[B] = set
  override def and[B >: Nothing](set: Multiset[B]): Multiset[Nothing] = Nil
  override def filter(f: Nothing => Boolean): Multiset[Nothing] = Nil
  override def map[B](f: Nothing => B): Multiset[B] = Nil
  override def flatMap[B](f: (Nothing) => Multiset[B]): Multiset[B] = Nil

  override def toSeq: Seq[Nothing] = List.empty
  override def toString: String = "Nil"
}

object Multiset {

  def apply[A](elements: A*): Multiset[A] = {
    elements.foldLeft[Multiset[A]](Nil)((set, elem) => set.+(elem))
  }

  def unapplySeq[A](set: Multiset[A]): Option[Seq[A]] = {
    if (set.equals(Nil))
      return None
    // Make order more predictable, as after or/and/flatMap operations it can be unexpected
    Some(set.toSeq.sortBy(_.hashCode()))
  }

  /*
  Make normal apply. This also guaranties us that something like Multiset("String")(1) wont compile.
   */
  implicit class ApplicableMultiset[A](s: Multiset[A]) extends (A => Boolean) {
    def apply(a: A): Boolean = s.contains(a)
  }

}
