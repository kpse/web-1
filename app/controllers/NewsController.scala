package controllers

import play.api.mvc._
import play.api.libs.json.Json
import models._
import play.api.data.Form
import play.api.data.Forms._
import models.SuccessResponse
import models.NewsPreview

object NewsController extends Controller with Secured {
  implicit val writes = Json.writes[News]
  implicit val writes2 = Json.writes[SuccessResponse]
  implicit val writes3 = Json.writes[NewsPreview]
  implicit val writes4 = Json.writes[ErrorResponse]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int], classId: Option[String]) = IsLoggedIn {
    u => _ =>
      val jsons = News.allSorted(kg, classId, from, to).take(most.getOrElse(25))
      Ok(Json.toJson(jsons))
  }

  def show(kg: Long, newsId: Long) = IsLoggedIn {
    u => _ =>
      News.findById(kg, newsId).fold(NotFound(""))(news =>
        Ok(Json.toJson(news)))
  }

  val newsForm = Form(
    tuple(
      "news_id" -> longNumber,
      "school_id" -> longNumber,
      "title" -> text,
      "content" -> text,
      "published" -> boolean,
      "class_id" -> optional(number),
      "image" -> optional(text),
      "publisher_id" -> optional(text),
      "feedback_required" -> optional(boolean)
    )
  )

  def update(kg: Long, newsId: Long) = IsLoggedIn {
    u => implicit request =>
      newsForm.bindFromRequest.value.fold(BadRequest(""))({
        news =>
          val updated = News.update(news, kg)
          Ok(Json.toJson(updated))
      })
  }

  def adminUpdate(kg: Long, employeeId: String, newsId: Long) = IsLoggedIn {
    u => implicit request =>
      Employee.canAccess(Some(employeeId), kg) match {
        case false => Forbidden(Json.toJson(ErrorResponse("您无权修改这条公告。")))
        case true =>
          newsForm.bindFromRequest.value.fold(BadRequest(""))({
            case (news) =>
              val updated = News.update(news, kg)
              Ok(Json.toJson(updated))
          })
      }

  }

  def indexWithNonPublished(kg: Long, employeeId: String, class_id: Option[String], restrict: Option[Boolean], from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn {
    u => _ =>
      Employee.canAccess(Some(employeeId), kg) match {
        case false => Forbidden(Json.toJson(ErrorResponse("您无权查看学校公告。")))
        case true =>
          val jsons = News.allIncludeNonPublished(kg, class_id, restrict.getOrElse(false), from, to).take(most.getOrElse(25))
          Ok(Json.toJson(jsons))
      }
  }

  def delete(kg: Long, employeeId: String, newsId: Long) = IsLoggedIn {
    u => _ =>
      Employee.canAccess(Some(employeeId), kg) match {
        case false => Forbidden(Json.toJson(ErrorResponse("您无权查看学校公告。")))
        case true =>
          News.delete(newsId)
          Ok(Json.toJson(new SuccessResponse))
      }
  }

  val newsCreateForm = Form(
    tuple(
      "school_id" -> longNumber,
      "title" -> text,
      "content" -> text,
      "published" -> optional(boolean),
      "class_id" -> optional(number),
      "image" -> optional(text),
      "publisher_id" -> optional(text),
      "feedback_required" -> optional(boolean)
    )
  )

  def create(kg: Long, employeeId: String) = IsLoggedIn {
    u => implicit request =>
      Employee.canAccess(Some(employeeId), kg) match {
        case false => Forbidden(Json.toJson(ErrorResponse("您无权创建本校公告。")))
        case true =>
          newsCreateForm.bindFromRequest.value.fold(BadRequest(""))({
            news =>
              val created = News.create(news)
              Ok(Json.toJson(created))
          })
      }
  }

  def deleteOne(kg: Long, newsId: Long) = IsLoggedIn {
    u => _ =>
      News.delete(newsId)
      Ok(Json.toJson(new SuccessResponse))
  }

  def previewNonPublished(kg: Long, employeeId: String, class_id: Option[String], restrict: Option[Boolean]) = IsLoggedIn {
    u => _ =>
      Employee.canAccess(Some(employeeId), kg) match {
        case false => Forbidden(Json.toJson(ErrorResponse("您无权查看本校公告。")))
        case true =>
          val jsons = News.previewAllIncludeNonPublished(kg, class_id, restrict.getOrElse(false))
          Ok(Json.toJson(jsons))
      }
  }

}