package ru.spbau.jvm.scala.task03

import akka.actor.{ActorSystem, Props}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import ru.spbau.jvm.scala.task03.bot.TwitchNotifyBot
import ru.spbau.jvm.scala.task03.database.DatabaseActor
import ru.spbau.jvm.scala.task03.twitch.TwitchPoller

object Main extends App {

  private val token = "296295927:AAGUKJIzpAbXtlfevUwHEaF7Q26KIK6qAYk"

  val system = ActorSystem()
  val scheduler = QuartzSchedulerExtension(system)
  val database = system.actorOf(Props(classOf[DatabaseActor]))
  val bot = new TwitchNotifyBot(token, database)
  val poller = system.actorOf(Props(classOf[TwitchPoller], bot, database))

  scheduler.createSchedule("every minute", None, "	0 * * * * ? *")
  scheduler.schedule("every minute", poller, "Poll")

  bot.run()

}
