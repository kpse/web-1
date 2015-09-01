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
    play.api.Play.mode(app) match {
      case play.api.Mode.Test =>
        Logger(classOf[CronJob]).info("No CronJob in test mode")
      case _ =>
        Logger(classOf[CronJob]).info("start cron jobs")

        val monitorActor = Akka.system.actorOf(Props[CronJob], name = "CronJob")

        Akka.system.scheduler.schedule(2 minutes, 1 hours, monitorActor, Tick)
    }

  }
}


