package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller


case class ImmuneStudent(id: Option[Long], immune_id: Option[Long], student_id: Option[Long])

object ImmuneStudentController extends Controller with Secured {

  implicit val writeImmuneStudent = Json.writes[ImmuneStudent]
  implicit val readImmuneStudent = Json.reads[ImmuneStudent]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(ImmuneStudent(Some(1), Some(1), Some(1)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(ImmuneStudent(Some(id), Some(1), Some(1))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ImmuneStudent].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[ImmuneStudent].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
*
create table IMSInfo.dbo.ImmuneStudent(
SysNO int identity(1,1),系统编号
RecordSysNO int,疫苗记录编号
StudentSysNO int学生编号
)

*/
