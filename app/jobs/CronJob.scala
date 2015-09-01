package jobs

import akka.actor.Actor
import models.School
import models.V4.{AgentSchool, AgentStatistics, KulebaoAgent}
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.Logger

case object Tick


class CronJob extends Actor {
  private val log = Logger(classOf[CronJob])

  def receive = {
    case Tick if DateTime.now().dayOfMonth().get() == 1 => {
      log.info("Got tick at the first day of the month")

      KulebaoAgent.monthlyStatistics
    }
    case Tick =>
      log.info("Got a normal tick")
  }

}
