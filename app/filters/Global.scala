import akka.actor.{Props, Actor}
import filters.LoggingFilter
import jobs.{CronJob, Tick}
import play.api.{Application, Logger, GlobalSettings}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.libs.Akka
import scala.concurrent.duration._

object Global extends WithFilters(LoggingFilter) with GlobalSettings {
  // ...
  override def onStart(app: Application): Unit = {

    Logger(classOf[CronJob]).info("Test code")

    val monitorActor = Akka.system.actorOf(Props[CronJob], name = "CronJob")

    Akka.system.scheduler.schedule(10 minutes, 1 hour, monitorActor, Tick)
  }
}


