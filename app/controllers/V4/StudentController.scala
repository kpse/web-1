package controllers.V4

import controllers.helper.CacheHelper._
import controllers.{RelationshipController, Secured}
import models.Children.readChildInfo
import models.V3.Student
import models._
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller


object StudentController extends Controller with Secured {
  implicit val RelativesCacheKey = "index_v3_students"
  createKeyCache

  def clearCurrentCache(kg: Long) = {
    clearStudentCache(kg)
    RelationshipController.clearCurrentCache(kg)
  }

  def clearStudentCache(kg: Long = 0) = kg match {
    case 0 => clearAllCache(s"Student_")
    case _ => clearAllCache(s"Student_$kg")
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    val cacheKey: String = s"Student_${kg}_${from}_${to}_${most}"
    Logger.info(s"StudentController entering index = ${cacheKey}")

    val students: List[Student] = digFromCache[List[Student]](cacheKey, 600, () => {
      Student.index(kg, from, to, most)
    })
    Ok(Json.toJson(students.map(_.checkCachedStatus)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Student.showWithCachedStatus(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的学生。(No such student)")))
    }

  }
}