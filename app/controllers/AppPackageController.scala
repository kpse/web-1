package controllers

import play.api.mvc.{SimpleResult, Action, Controller}
import play.api.data.Form
import play.api.data.Forms._
import models.{ErrorResponse, SuccessResponse, AppPackage}
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
    Ok(views.html.download("幼乐宝下载页面",
      "https://itunes.apple.com/us/app/you-le-baoios/id854211863?ls=1&mt=8",
      "http://a.app.qq.com/o/simple.jsp?pkgname=com.cocobabys"))
  }

  def downloadTeacherClient = Action {
    Ok(views.html.download("幼教宝下载页面", "https://itunes.apple.com/us/app/you-jiao-bao/id917314512?ls=1&mt=8", "http://a.app.qq.com/o/simple.jsp?pkgname=com.cocobaby.teacher"))
  }


  def lastTeacherApp(redirect: Option[String]) = Action {
    packageFor(redirect, Some("teacher"))
  }

  def lastPCPackage(redirect: Option[String]) = Action {
    packageFor(redirect, Some("pc"))
  }

  def packageFor(redirect: Option[String], userType: Some[String] = Some("parent")): SimpleResult = {
    redirect.fold(BadRequest(Json.toJson(ErrorResponse("Error redirect option", 112))))({
      case "true" =>
        AppPackage.latest(userType).fold(BadRequest(Json.toJson(ErrorResponse("No such package at all", 113))))({
          case pkg if pkg.url.startsWith("http") =>
            Redirect(pkg.url)
        })
      case _ => Ok(Json.toJson(AppPackage.latest(userType)))
    })
  }

  def delete(packageId: Long) = IsOperator {
    u => _ =>
      val delete1: Boolean = AppPackage.delete(packageId)
      delete1 match {
        case true =>
          Ok(Json.toJson(new SuccessResponse))
        case false =>
          Ok(Json.toJson(ErrorResponse("App deleting error..")))
      }

  }

  def downloadWarning() = Action {
    Ok(views.html.warningDownload("产品升级公告", "/app_package/download_v2"))
  }

  def downloadWarningTeacher() = Action {
    Ok(views.html.warningDownload("产品升级公告", "/teacher_app_package/download_v2"))
  }
}
