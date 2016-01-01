package jobs

import akka.actor.Actor
import models.School
import models.V4.{SchoolOperationReport, AgentSchool, AgentStatistics, KulebaoAgent}
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.Logger

case object Tick


class CronJob extends Actor {
  private val log = Logger(classOf[CronJob])

  def receive = {
    case Tick if DateTime.now().dayOfMonth().get() == 1 && DateTime.now().hourOfDay().get < 2 => {
      log.info(s"Got tick at the first day of the month, do Agent stats at ${DateTime.now()}")
      KulebaoAgent.monthlyStatistics
    }
    case Tick if DateTime.now().dayOfMonth().get() == 1 && DateTime.now().hourOfDay().get < 5 => {
      log.info(s"Got tick at the first day of the month, do School stats at ${DateTime.now()}")
      SchoolOperationReport.monthlyStatistics
    }
    case Tick if DateTime.now().hourOfDay().get() < 5 => {
      log.info(s"Got tick at the first hours of the day ${DateTime.now().hourOfDay().get()}")

      SchoolOperationReport.dailyStatistics
    }
    case Tick =>
      log.info("Got a normal tick")
  }

}
