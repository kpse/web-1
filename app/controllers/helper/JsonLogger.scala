package controllers.helper

import play.api.libs.json.{JsObject, JsError, Json, JsValue}
import play.api.Logger

object JsonLogger {

  def loggedJson[T](t: T)(implicit tjs : play.api.libs.json.Writes[T]): JsValue = {
    val json = Json.toJson(t)
    Logger.info(json.toString)
    json
  }

  def loggedErrorJson[T](e: JsError): JsObject = {
    val json = JsError.toFlatJson(e)
    Logger.info(json.toString)
    json
  }
}
