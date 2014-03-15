package models

abstract class JsonResponse(error_code: Int)

case class ErrorResponse(error_msg: String, error_code: Int = 1) extends JsonResponse(1)

case class SuccessResponse(error_msg: String = "", error_code: Int = 0) extends JsonResponse(0)