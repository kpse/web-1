package models

abstract class JsonResponse(error_code: Int)

case class ErrorResponse(error_msg: String) extends JsonResponse(1)

case class SuccessResponse(error_msg: String = "") extends JsonResponse(0)