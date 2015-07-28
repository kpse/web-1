package jobs

import akka.actor.Actor
import models.School
import models.V4.{AgentStatistics, KulebaoAgent}
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.Logger

case object Tick


class CronJob extends Actor {
  private val log = Logger(classOf[CronJob])

  def receive = {
    case Tick => {
      log.info("Got tick")
      val r = scala.util.Random
      val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")
      val lastMonth: String = pattern.print(DateTime.now().minusMonths(r.nextInt(12)))
      Logger.info(s"lastMonth = ${lastMonth}")
      SchoolIntro.index.map {
        s =>
          KulebaoAgent.collectData(AgentStatistics(0, 1, s.school_id, lastMonth, r.nextInt(100), 100, 0))
      }

    }
  }
}
