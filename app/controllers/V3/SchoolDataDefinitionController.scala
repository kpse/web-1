package controllers.V3

import controllers.Secured
import models.V3.MedicineRecord
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class SchoolDataDefinition(id: Option[Long], school_id: Long, name: String, `type`: String, created_at: Option[Long]) {
  def update(kg: Long, id: Long): Option[SchoolDataDefinition] = Some(this.copy(id = Some(id), school_id = kg))

  def create(kg: Long): Option[SchoolDataDefinition] = Some(this.copy(id = Some(1), school_id = kg))
}

object SchoolDataDefinition {
  implicit val readSchoolDataDefinition = Json.reads[SchoolDataDefinition]
  implicit val writeSchoolDataDefinition = Json.writes[SchoolDataDefinition]

  def show(kg: Long, id: Long): Option[SchoolDataDefinition] = Some(SchoolDataDefinition(Some(id), kg, "名称", "类型", Some(System.currentTimeMillis())))

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = List(SchoolDataDefinition(Some(1), kg, "名称", "类型", Some(System.currentTimeMillis())))

  def deleteById(kg: Long, id: Long) = None
}

object SchoolDataDefinitionController extends Controller with Secured {
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(SchoolDataDefinition.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolDataDefinition.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的数据定义。(No such school enumeration)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolDataDefinition].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolDataDefinition].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg, id)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolDataDefinition.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}