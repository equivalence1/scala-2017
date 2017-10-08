package ru.spbau.jvm.scala.task03.bot

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.util.Success
import info.mukel.telegrambot4s.api.{Polling, TelegramBot}
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.models.Message
import ru.spbau.jvm.scala.task03.database.DatabaseActor._

final class TwitchNotifyBot(val token: String, database: ActorRef) extends TelegramBot with Commands with Polling {

  // twitch_notify_bot

  onCommand("/start") {
    implicit msg => {
      showHelp
    }
  }

  onCommand("/add") {
    implicit msg => withArgs { args =>
      args.foreach(name => {
        if (!name.matches("\\w+"))
          reply(s"'$name' is not a valid name for a twitch channel")
        else {
          database ! AddChannel(msg.chat.id, name)
          reply(s"'$name' channel has been successfully added to your list")
        }
      })
    }
  }

  onCommand("/remove") {
    implicit msg => withArgs { args =>
      args.foreach(name => {
        if (!name.matches("\\w+"))
          reply(s"'$name' is not a valid name for a twitch channel")
        else {
          implicit val timeout: Timeout = Timeout(5.second)
          (database ? HasPair(msg.chat.id, name)).onComplete {
            case Success(BoolResult(true)) =>
              database ! RemoveChannel(msg.chat.id, name)
              reply(s"'$name' channel has been successfully removed from your list")
            case Success(BoolResult(false)) =>
              reply(s"'$name' is not in your channel list")
            case _ => reply("database error")
          }
        }
      })
    }
  }

  onCommand("/list") {
    implicit msg => {
      implicit val timeout: Timeout = Timeout(5.second)
      (database ? GetChannels(msg.chat.id)).onComplete {
        case Success(ChannelsList(list)) => reply(list.mkString("\n"))
        case _ => reply("database error")
      }
    }
  }

  onCommand("/help") {
    implicit msg => {
      showHelp
    }
  }

  private def showHelp(implicit msg: Message): Unit = {
    reply("/list -- list of channels you are tracking\n" +
          "/add channel_name -- add a new channel\n" +
          "/remove channel_name -- remove a channel\n")
  }

}
