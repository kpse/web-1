package models

import play.api.libs.json.Json

abstract class JsonResponse(error_code: Int)

case class ErrorResponse(error_msg: String, error_code: Int = 1) extends JsonResponse(1)

case class SuccessResponse(error_msg: String = "", error_code: Int = 0) extends JsonResponse(0)

case class ForceUpdateResponse(android_link: String="/app_package?redirect=true", ios_link: String="https://itunes.apple.com/us/app/you-le-baoios/id854211863?ls=1&mt=8", error_msg: String = "客户端版本过低，请升级您的客户端", error_code: Int = 999) extends JsonResponse(999)

object ErrorResponse {
  implicit val writeErrorResponse = Json.writes[ErrorResponse]
  implicit val readErrorResponse = Json.reads[ErrorResponse]
}

object SuccessResponse {
  implicit val writeSuccessResponse = Json.writes[SuccessResponse]
  implicit val readSuccessResponse = Json.reads[SuccessResponse]
}