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
    case Tick => {
      log.info("Got tick")
      val r = scala.util.Random
      val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")
      val lastMonth: String = pattern.print(DateTime.now().minusMonths(r.nextInt(12)))
      Logger.info(s"lastMonth = $lastMonth")

      KulebaoAgent.index(None, None, None).foreach {
        case agent =>
          AgentSchool.index(agent.id.get, None, None, None).foreach {
            case school =>
              Logger.info(s"${agent.id.get}, ${school.school_id}, $lastMonth, ${r.nextInt(100)}")
              KulebaoAgent.collectData(AgentStatistics(0, agent.id.get, school.school_id, lastMonth, r.nextInt(100), 100, 0))
          }
      }
    }
  }
}
