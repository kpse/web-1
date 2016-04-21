package controllers

import models.V8.KulebaoNews
import play.api.mvc._

object HomePageController extends Controller with Secured {

  def newHome = Action {
    Ok(views.html.v2.home())
  }

  def software = Action {
    Ok(views.html.v2.software())
  }

  def hardware = Action {
    Ok(views.html.v2.hardware())
  }

  def becomeUser = Action {
    Ok(views.html.v2.becomeUser())
  }

  def becomeAgent = Action {
    Ok(views.html.v2.becomeAgent())
  }

  def aboutUs = Action {
    Ok(views.html.v2.aboutUs())
  }

  def newsList = Action {
    Ok(views.html.v2.newsList(KulebaoNews.top10News))
  }

  def singleNews(id: Long) = Action {
    KulebaoNews.findById(id) match {
      case Some(news) =>
        Ok(views.html.v2.singleNews(news, KulebaoNews.top5News, KulebaoNews.prevId(news), KulebaoNews.nextId(news)))
      case None =>
        Ok(views.html.v2.notFound())
    }
  }

  def underConstruction = Action {
    Ok(views.html.v2.underConstruction())
  }

}

