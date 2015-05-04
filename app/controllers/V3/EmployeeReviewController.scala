package controllers.V3

import controllers.Secured
import models.SuccessResponse
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller


case class EmployeeReview(id: Option[Long], employee_id: Option[String], type_id: Option[String], type_name: Option[String], created_at: Option[Long], comment: Option[String])

object EmployeeReviewController extends Controller with Secured {

  implicit val writeEmployeeReview = Json.writes[EmployeeReview]
  implicit val readEmployeeReview = Json.reads[EmployeeReview]

  def index(kg: Long, employeeId: String) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(EmployeeReview(Some(1), Some(employeeId), Some("123"), Some("年终评定"), Some(System.currentTimeMillis), Some("一塌糊涂")))))
  }

  def show(kg: Long, employeeId: String, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(EmployeeReview(Some(id), Some(employeeId), Some("123"), Some("年终评定"), Some(System.currentTimeMillis), Some("一塌糊涂"))))
  }

  def create(kg: Long, employeeId: String) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[EmployeeReview].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, employeeId: String, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[EmployeeReview].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, employeeId: String, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
*
create table IMSInfo.dbo.EmployeeReview(
SysNO int identity(1,1),系统编号
EmployeeSysNO int,员工编号
ReviewTypeSysNO int,测评结果编号
ReviewTypeName nvarchar(20),测评结果名称
ReviewTime datetime,测评时间
ReviewContent nvarchar(1000)测评内容
)

*/
