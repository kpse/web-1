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

      KulebaoAgent.index(None, None, None).foreach {
        case agent =>
          AgentSchool.index(agent.id.get, None, None, None).foreach {
            case school =>
              val monthData: AgentStatistics = KulebaoAgent.collectTheWholeMonth(agent.id.get, school.school_id, DateTime.now().minusMonths(1))
              Logger.info(s"${agent.id.get}, ${school.school_id}, ${monthData.logged_once}, ${monthData.logged_ever}")
              KulebaoAgent.collectData(monthData)
          }
      }
    }
    case Tick =>
      log.info("Got a normal tick")
  }
}
