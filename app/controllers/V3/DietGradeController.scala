package controllers.V3

import controllers.Secured
import models.SuccessResponse
import models.V3.{Metal, Vitamin, DietGrade}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object DietGradeController extends Controller with Secured {

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(List(DietGrade(Some(1), Some("吃的"), Some("不明白"), Some("5"), Some("11"), Some(1), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some("10"), Some("19"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19")))))))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(DietGrade(Some(id), Some("吃的"), Some("不明白"), Some("5"), Some("11"), Some(1), Some("2"), Some("3"), Some("4"), Some("5"), Some("6"), Some("7"), Some("8"),Some("8"),
      Some(Vitamin(Some("21"), Some("221"), Some("21"), Some("12"), Some("12"), Some("31"), Some("14"), Some("12"), Some("12"), Some("12"), Some("12"), Some("12")))
      , Some("9"), Some("10"), Some("19"), Some(Metal(Some("11"), Some("12"), Some("13"), Some("14"), Some("15"), Some("61"), Some("17"), Some("18"), Some("19"), Some("19"), Some("19"))))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietGrade].map {
      case (s) =>
        Ok(Json.toJson(s))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[DietGrade].map {
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
--膳食等级
      create table IMSInfo.dbo.DietGrade(
                            SysNO int identity(1,1),系统编号
                            Name nvarchar(20), 等级名称
                            AgeName nvarchar(20),年龄段名称
                            AgeMin nvarchar(20),最小年龄
                            AgeMax nvarchar(20),最大年龄
                            Gender nvarchar(20),性别
                            Labor nvarchar(20),工作强度
                            Joule nvarchar(20),焦耳
                            Calorie nvarchar(20),卡路里
                            Protein nvarchar(20),下面都一样
                            Fat nvarchar(20),
                            Ca nvarchar(20),
                            P nvarchar(20),
                            K nvarchar(20),
                            Na nvarchar(20),
                            Mg nvarchar(20),
                            Fe nvarchar(20),
                            I nvarchar(20),
                            Zn nvarchar(20),
                            Se nvarchar(20),
                            Cu nvarchar(20),
                            F nvarchar(20),
                            Cr nvarchar(20),
                            Mn nvarchar(20),
                            Mo nvarchar(20),
                            VA nvarchar(20),
                            VB1 nvarchar(20),
                            VB2 nvarchar(20),
                            VB3 nvarchar(20),
                            VB5 nvarchar(20),
                            VB6 nvarchar(20),
                            VB9 nvarchar(20),
                            VB12 nvarchar(20),
                            VC nvarchar(20),
                            VD nvarchar(20),
                            VE nvarchar(20),
                            Choline nvarchar(20),
                            VH nvarchar(20)
                            )
 */