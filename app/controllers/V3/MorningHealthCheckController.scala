package controllers.V3

import controllers.Secured
import controllers.V3.SmsGroupManagementController._
import models.{ErrorResponse, SuccessResponse}
import models.V3.{StudentHealthCheckDetail, HealthCheck}
import models.V3.HealthCheck._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller


case class MorningHealthCheck(id: Option[Long], recorder_id: Option[Long], writer_id: Option[Long], checked_at: Option[Long], memo: Option[String])

object MorningHealthCheckController extends Controller with Secured {

  implicit val writeMorningHealthCheck = Json.writes[MorningHealthCheck]
  implicit val readMorningHealthCheck = Json.reads[MorningHealthCheck]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(MorningHealthCheck(Some(1), Some(1), Some(1), Some(System.currentTimeMillis), Some("某次晨检")))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(MorningHealthCheck(Some(id), Some(1), Some(1), Some(System.currentTimeMillis), Some("某次晨检"))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MorningHealthCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[MorningHealthCheck].map {
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
create table IMSInfo.dbo.MorningCheckRecord(
SysNO int identity(1,1),系统编号
Memo nvarchar(500),备注
RecordTime datetime,晨检时间
RecorderSysNO int,操作老师编号
WriterSysNO int填表老师编号
)
*/

case class StudentHealthCheck(id: Option[Long], record_id: Option[Long], student_id: Option[Long], temperature: Option[String],
                              check_status_id: Option[Long], check_status_name: Option[String], relative_words: Option[String],
                              memo: Option[String])

object StudentMorningHealthCheckController extends Controller with Secured {

  implicit val writeStudentHealthCheck = Json.writes[StudentHealthCheck]
  implicit val readStudentHealthCheck = Json.reads[StudentHealthCheck]

  def index(kg: Long, studentId: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(StudentHealthCheck(Some(1), Some(1), Some(studentId), Some("37.4C"), Some(1), Some("晨检结果名称"), Some("家长能说啥？"), Some("这次晨检的备忘")))))
  }

  def show(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(StudentHealthCheck(Some(id), Some(1), Some(studentId), Some("37.4C"), Some(1), Some("晨检结果名称"), Some("家长能说啥？"), Some("这次晨检的备忘"))))
  }

  def create(kg: Long, studentId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, studentId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheck].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
create table IMSInfo.dbo.MorningCheckStudent(
SysNO int identity(1,1),系统编号
RecordSysNO int,晨检记录编号
StudentSysNO int,学生编号
Temperature varchar(20),温度
CheckStatusSysNO int,晨检结果编号
CheckStatusName nvarchar(20),晨检结果名称
RelativeWords nvarchar(500),家长嘱咐
Memo nvarchar(500)备注
)
 */

object StudentHealthCheckController extends Controller with Secured {

  def index(kg: Long, studentId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(HealthCheck.index(kg, studentId, from, to, most)))
  }

  def show(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    HealthCheck.show(kg, studentId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的体检报告。(No such student health check result)")))
    }

  }

  def create(kg: Long, studentId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheckDetail].map {
      case (s) =>
        Ok(Json.toJson(s.create(kg, studentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, studentId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StudentHealthCheckDetail].map {
      case (s) if s.id.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("更新资源请带ID(update requires id)", 2)))
      case (s) if s.id.get != id =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(ids are not matched)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, studentId, id)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, studentId: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
create table IMSInfo.dbo.PhysicalExamination(
SysNO int identity(1,1),系统编号
RecordName nvarchar(50),体检名称
RecordTime datetime,体检时间
StudentSysNO int,学生编号
Basic_Height nvarchar(50),--身高
Basic_Weight nvarchar(50),--体重
Head_Eye_LeftEyeSight nvarchar(50),--左眼视力
Head_Eye_RightEyeSight nvarchar(50),--右眼视力
Head_Eye_Trachoma nvarchar(50),--沙眼
Head_Eye_Conjunctivitis nvarchar(50),--结膜炎
Head_Ear_LeftEar nvarchar(50),--左耳听力
Head_Ear_RightEar nvarchar(50),--右耳听力
Head_Tooth_QTY nvarchar(50),--牙齿数量
Head_Tooth_Caries nvarchar(50),--龋齿
Head_Tooth_Periodontal nvarchar(50),--牙周
Surgery_HeadCircumference nvarchar(50),--头围
Surgery_ChestCircumference nvarchar(50),--胸围
Surgery_Limbs nvarchar(50),--四肢
Surgery_Spine nvarchar(50),--脊柱
Surgery_Skin nvarchar(50),--皮肤
Surgery_Lymphaden nvarchar(50),--淋巴结
Medicine_LeftTonsil nvarchar(50),--左扁桃体
Medicine_RightTonsil nvarchar(50),--右扁桃体
Medicine_BloodPressure nvarchar(50),--血压
Medicine_BloodTypeSysNO nvarchar(50),--血型编号
Medicine_BloodTypeName nvarchar(50),--血型名称
Medicine_Hemoglobin nvarchar(50),--血红蛋白素
Medicine_Heart nvarchar(50),--心
Medicine_Liver nvarchar(50),--肝
Medicine_Spleen nvarchar(50),--脾
Medicine_Lung nvarchar(50),--肺
Other_Rickets_ChongMen nvarchar(50),--囱门
Other_Rickets_FangTou nvarchar(50),--方头
Other_Rickets_PingPongTou nvarchar(50),--乒乓头
Other_Rickets_ZhenTu nvarchar(50),--枕秃
Other_Rickets_JiXiong nvarchar(50),--鸡胸
Other_Rickets_LeChuanZhu nvarchar(50),--肋串珠
Other_Rickets_HaoShiGou nvarchar(50),--郝氏沟
Other_Rickets_YiJingDuoHan nvarchar(50),--易惊多汗
Other_Rickets_XO nvarchar(50),--XO型腿
Other_InfantileParalysis nvarchar(50),--小儿麻痹症
Other_Ascarid nvarchar(50),--蛔虫病
Other_Skins nvarchar(50),--各种皮肤病
Other_Deformity nvarchar(50),--各种畸形
Other_Psychosis nvarchar(50),--精神性疾病
Other_Hernia nvarchar(50),--疝气
Other_Others nvarchar(50),--其他
Memo nvarchar(500)--总结
)
 */