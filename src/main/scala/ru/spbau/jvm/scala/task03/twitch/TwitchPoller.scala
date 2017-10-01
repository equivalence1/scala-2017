package ru.spbau.jvm.scala.task03.twitch

import akka.pattern.ask
import akka.actor.{Actor, ActorRef}
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.util.Success
import info.mukel.telegrambot4s.api.TelegramBot
import info.mukel.telegrambot4s.methods.SendMessage
import org.json4s.JsonAST.JNull
import org.json4s.jackson.JsonMethods
import ru.spbau.jvm.scala.task03.database.DatabaseActor.{ChannelsList, GetChannels, GetIds, IdList}

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.collection.mutable
import scalaj.http.Http

class TwitchPoller(bot: TelegramBot, database: ActorRef) extends Actor {

  import ChannelStatus._

  private val lastStatus: mutable.HashMap[(Long, String), ChannelStatus] =
    mutable.HashMap.empty

  private val streamUrlPrefix = "https://www.twitch.tv/"
  private val twitchApiPrefix = "https://api.twitch.tv/kraken/streams/"
  private val clientId = "3fft62444ezsce9kafe1ot7abbioln"

  private def getChannelStatus(str: String): ChannelStatus = {
    val streamInfo: String = Http(twitchApiPrefix + str).header("Client-ID", clientId).asString.body

    val parsedJson = JsonMethods.parse(streamInfo)
    parsedJson \ "stream" match {
      case JNull => Offline
      case _ => Online
    }
  }

  private def notifyUser(id: Long, name: String): Unit = {
    bot.request(SendMessage(id, s"Channel $streamUrlPrefix$name is now streaming!"))
  }

  override def receive: Actor.Receive = {
    case _ =>
      implicit val timeout: Timeout = Timeout(5.second)

      (database ? GetIds()).onComplete {
        case Success(IdList(list)) => list.foreach(id =>
          (database ? GetChannels(id)).onComplete {
            case Success(ChannelsList(channels)) =>
              channels.foreach(channel => {
                val newStatus: ChannelStatus = getChannelStatus(channel)
                if (newStatus == Online && lastStatus.getOrElse((id, channel), Offline) == Offline) {
                  notifyUser(id, channel)
                  lastStatus.put((id, channel), newStatus)
                }
              })
            case _ =>
          }
        )
        case _ =>
      }
  }

}

private object ChannelStatus extends Enumeration {
  type ChannelStatus = Value
  val Online, Offline = Value
}