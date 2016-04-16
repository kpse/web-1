package controllers

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

  def underConstruction = Action {
    Ok(views.html.v2.underConstruction())
  }

}

