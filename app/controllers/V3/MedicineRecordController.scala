package controllers.V3

import controllers.Secured
import models.V3.Hardware
import models.{SuccessResponse, ErrorResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller


case class MedicineRecord(id: Option[Long], student_id: Long, title: String, content: String, updated_at: Option[Long]) {
  def create(kg: Long) = MedicineRecord(Some(1), student_id, title, content, updated_at)

  def update(kg: Long) = MedicineRecord(id, student_id, title, content, updated_at)
}

object MedicineRecord {
  implicit val readMedicineRecord = Json.reads[MedicineRecord]
  implicit val writeMedicineRecord = Json.writes[MedicineRecord]

  def show(kg: Long, id: Long): Option[MedicineRecord] = Some(MedicineRecord(Some(id), 1, "吃药了", "不是很想吃", Some(System.currentTimeMillis() - 10000)))

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = List(MedicineRecord(Some(1), 1, "吃药了", "不是很想吃", Some(System.currentTimeMillis() - 10000)))

  def deleteById(kg: Long, id: Long) = None

}

object MedicineRecordController extends Controller with Secured {
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(MedicineRecord.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    MedicineRecord.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的吃药记录。(No such medicine record)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MedicineRecord].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MedicineRecord].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    MedicineRecord.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}