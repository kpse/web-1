package controllers.V3

import controllers.Secured
import models.ChatSession
import play.api.libs.json.Json
import play.api.mvc.Controller

object HistoryImagesController extends Controller with Secured {

  def index(kg: Long, timestamp: Long) = IsOperator { u => _ =>
    Ok(Json.toJson(ChatSession.allHistoryImages(kg, timestamp)))
  }

}