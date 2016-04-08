package controllers

import play.api.mvc._

object HomePageController extends Controller with Secured {

  def newHome = Action {
    Ok(views.html.v2.home())
  }

  def software = Action {
//    Ok(views.html.v2.software())
    Redirect("/constructing")
  }

  def hardware = Action {
    Ok(views.html.v2.hardware())
  }

  def becomeUser = Action {
//    Ok(views.html.v2.becomeUser())
    Redirect("/constructing")
  }

  def becomeAgent = Action {
//    Ok(views.html.v2.becomeAgent())
    Redirect("/constructing")
  }

  def aboutUs = Action {
//    Ok(views.html.v2.aboutUs())
    Redirect("/constructing")
  }

  def underConstruction = Action {
    Ok(views.html.v2.underConstruction())
  }

}

