package ru.spbau.jvm.scala.task03.database

import akka.persistence.{PersistentActor, SnapshotOffer}

import scala.collection.mutable

class DatabaseActor extends PersistentActor {

  import DatabaseActor._

  override def persistenceId = "twitch-notify-database"

  type Collection = mutable.HashMap[Long, mutable.HashSet[String]]

  var map: Collection =
    mutable.HashMap.empty

  def receiveEvent(evt: PersistentEvent): Unit = {
    evt match {
      case AddChannel(id: Long, name: String) =>
        map.getOrElseUpdate(id, mutable.HashSet.empty) += name
      case RemoveChannel(id: Long, name: String) =>
        map.getOrElseUpdate(id, mutable.HashSet.empty) -= name
    }
  }

  override def receiveRecover: Receive = {
    case evt: PersistentEvent => receiveEvent(evt)
    case SnapshotOffer(_, snapshot: Collection) => map = snapshot
  }

  val snapshotInterval = 1000

  override def receiveCommand: Receive = {
    case evt: PersistentEvent => persist(evt)(receiveEvent)
    case GetChannels(id) => sender ! ChannelsList(map.getOrElse(id, mutable.HashSet.empty))
    case GetIds() => sender ! IdList(map.keys.toList)
    case HasPair(id, name) => sender ! BoolResult(map.contains(id) && map.apply(id).contains(name))
  }

}

object DatabaseActor {

  trait PersistentEvent
  case class AddChannel(id: Long, name: String) extends PersistentEvent
  case class RemoveChannel(id: Long, name: String) extends PersistentEvent
  case class GetChannels(id: Long)
  case class ChannelsList(list: mutable.HashSet[String])

  case class GetIds()
  case class IdList(list: List[Long])

  case class HasPair(id: Long, name: String)
  case class BoolResult(res: Boolean)
}
