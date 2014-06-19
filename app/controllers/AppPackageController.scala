package controllers

import play.api.mvc.{SimpleResult, Action, Controller}
import play.api.data.Form
import play.api.data.Forms._
import models.AppPackage
import play.api.libs.json.Json

object AppPackageController extends Controller with Secured {

  /*
  $scope.app =
  summary: ''
  url: 'http://cocobabys.oss-cn-hangzhou.aliyuncs.com/app_release/release_2.apk'
  size: 0
  version: 'V1.1'
  versioncode: 2
  remote_url: ''
  */
  val appCreateForm = Form(
    tuple(
      "summary" -> text,
      "url" -> text,
      "file_size" -> longNumber,
      "version_name" -> text,
      "version_code" -> number,
      "package_type" -> optional(text)
    )
  )
  implicit val write1 = Json.writes[AppPackage]

  def create = Action {
    implicit request =>
      appCreateForm.bindFromRequest.value.fold(BadRequest(""))({
        case app =>
          val created = AppPackage.create(app)
          Ok(Json.toJson(created))
      })
  }

  def last(redirect: Option[String]) = Action {
    packageFor(redirect)

  }

  def download = Action {
    Ok(views.html.download())
  }


  def lastTeacherApp(redirect: Option[String]) = Action {
    packageFor(redirect, Some("teacher"))
  }

  def packageFor(redirect: Option[String], userType: Some[String] = Some("parent")): SimpleResult = {
    redirect.fold(BadRequest(""))({
      case "true" =>
        AppPackage.latest(userType).fold(BadRequest(""))({
          case pkg if pkg.url.startsWith("http") =>
            Redirect(pkg.url)
        })
      case _ => Ok(Json.toJson(AppPackage.latest(userType)))
    })
  }
}
