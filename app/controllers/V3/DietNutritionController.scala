package controllers.V3

import controllers.Secured
import models.V3._
import models.V3.ArrangementGroup._
import models.V3.DietArrangement._
import models.V3.Menu._
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object DietNutritionController extends Controller with Secured {

  implicit val writeVitamin = Json.writes[Vitamin]
  implicit val readVitamin = Json.reads[Vitamin]
  implicit val writeMetal = Json.writes[Metal]
  implicit val readMetal = Json.reads[Metal]
  implicit val writeDietNutrition = Json.writes[DietNutrition]
  implicit val readDietNutrition = Json.reads[DietNutrition]

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(DietNutrition(Some(6001), Some(1), Some("吃的"), Some("好吃的"), Some("1kg"), Some("20g"), Some("1"), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19"))
      ), 1, Some(1)))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(DietNutrition(Some(id + 6000), Some(1), Some("吃的"), Some("好吃的"), Some("1kg"), Some("20g"), Some("1"), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19"))
      ), 0, Some(1))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietNutrition].map {
      case (s) =>
        Ok(Json.toJson(s.copy(id = Some(6001))))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietNutrition].map {
      case (s) =>
        Ok(Json.toJson(s.copy(id = Some(6001))))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(new SuccessResponse()))
  }
}

/*
* --膳食营养（本表根据网络数据库生成）
 create table IMSInfo.dbo.DietNutrition(
                            SysNO int identity(1,1),系统编号
                            FoodTypeSysNO int,所属食物编号
                            Name nvarchar(30),营养名称
                            AliasName nvarchar(20),缩写
                            Weight nvarchar(20),标准重量
                            AvailableWeight nvarchar(20),可食用部分重量
                            Calorie nvarchar(20),卡路里
                            Protein nvarchar(20),蛋白质
                            Fat nvarchar(20),脂肪
                            Carbohydrate nvarchar(20),碳水化合物
                            Ash nvarchar(20),灰分
                            Carotine nvarchar(20), 胡萝卜素
                            Fibre nvarchar(20),纤维
                            Cholesterol nvarchar(20),胆固醇
                            VA nvarchar(20),维a
                            VC nvarchar(20),维b
                            VD nvarchar(20), 维d
                            VE nvarchar(20), 维e
                            VB1 nvarchar(20), 维b1
                            VB2 nvarchar(20), 维b2
                            VB3 nvarchar(20), 维b3
                            VB9 nvarchar(20), 维b9
                            Ca nvarchar(20),钙
                            P nvarchar(20),磷
                            K nvarchar(20),钾
                            Na nvarchar(20),钠
                            Mg nvarchar(20),镁
                            Fe nvarchar(20),铁
                            Zn nvarchar(20),锌
                            Se nvarchar(20),硒
                            Cu nvarchar(20),铜
                            Mn nvarchar(20)锰
                            )

--膳食关系
create table IMSInfo.dbo.DietRelationship(
SysNO int identity(1,1),系统编号
MenuSysNO int,菜单编号
NutritionSysNO int,营养编号
Weight nvarchar(20)重量
)

*/
