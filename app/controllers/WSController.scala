package controllers

import com.qiniu.common.QiniuException
import com.qiniu.processing.OperationManager
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.json.{JsError, JsValue, Json}
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import models.News
import scala.Some
import play.api.{Play, Logger}
import com.qiniu.util.{UrlSafeBase64, StringMap, Auth => QiqiuAuth}
import java.net.URLEncoder
;

object WSController extends Controller {

  def call = Action.async {
    val url = "http://djcwebtest.duapp.com/forwardswipe.do"
    WS.url(url).post(postData(None)).map {
      response =>
        Ok(response.json)
    }
  }


  def postData(pushId: Option[String]): JsValue = {
    Json.obj(
      "timestamp" -> System.currentTimeMillis,
      "notice_type" -> 0,
      "child_id" -> "1_93740362_374",
      "pushid" -> Json.toJson(pushId.getOrElse("925387477040814447")),
      "record_url" -> "http://a.hiphotos.baidu.com/image/w%3D2048/sign=0ae610304890f60304b09b470d2ab21b/10dfa9ec8a136327e2b45e32938fa0ec08fac752.jpg"
    )
  }

  case class Swiped(uid: Long, phone: String, push_id: String)

  def sendMessage = Action.async(parse.json) {
    request =>
      val body: JsValue = request.body
      val pushId: String = (body \ "push_id").as[String]

      val url = "http://djcwebtest.duapp.com/forwardswipe.do"
      WS.url(url).post(postData(Some(pushId))).map {
        response =>
          Ok(response.json)
      }
  }

  def broadcastMessage(news: News): JsValue = {
    Json.obj(
      "timestamp" -> System.currentTimeMillis,
      "notice_type" -> 2,
      "push_tags" -> news.school_id,
      "notice_title" -> news.title,
      "notice_body" -> news.content,
      "publisher" -> "华德福幼儿园"
    )
  }

  def sendBroadcastMessage = Action.async(parse.json) {
    request =>
      val body: JsValue = request.body
      val schoolId: Long = (body \ "school_id").as[Long]
      val newsId: Long = (body \ "news_id").as[Long]
      val news = News.findById(schoolId, newsId)
      Logger.info("sending message of school %d, news %d".format(schoolId, newsId))
      if (news.nonEmpty) {
        val url = "http://djcwebtest.duapp.com/nmn.do"
        WS.url(url).post(broadcastMessage(news.get)).map {
          response =>
            Ok(response.json)
        }
      } else {
        Future {
          BadRequest("No such message.")
        }

      }
  }

  case class UpToken(token: String)


  implicit val writes1 = Json.writes[UpToken]

  def generateToken(bucket: String, key: Option[String]) = Action {
    val auth: QiqiuAuth = createAuth
    val upToken = auth.uploadToken(bucket, key.orNull, 3600, new StringMap().putNotEmpty("returnBody", "{\"name\": $(fname), \"size\": $(fsize),\"hash\": $(etag)}"))
    Ok(Json.toJson(UpToken(upToken)))
  }

  def generateEncodeToken(bucket: String, key: Option[String]) = key match {
    case Some(s) =>
      val encodedKey = URLEncoder.encode(s, "UTF-8")
      Logger.info("key = %s".format(encodedKey))
      generateToken(bucket, Some(encodedKey))
    case None =>
      generateToken(bucket, None)
  }

  case class Bucket(name: String, key: String) {
    def scope = s"$name:$key"
  }

  implicit val read1 = Json.reads[Bucket]

  def createAuth = {
    val ACCESS_KEY = Play.current.configuration.getString("oss.ak").getOrElse("")
    val SECRET_KEY = Play.current.configuration.getString("oss.sk").getOrElse("")
    Logger.info("ACCESS_KEY = %s, SECRET_KEY = %s".format(ACCESS_KEY, SECRET_KEY))
    QiqiuAuth.create(ACCESS_KEY, SECRET_KEY)
  }

  def generateSafeToken = Action(parse.json) {
    implicit request =>
      val auth: QiqiuAuth = createAuth
      request.body.validate[Bucket].map {
        case (bucket) =>
          val upToken = auth.uploadToken(bucket.name, bucket.key, 3600, new StringMap().putNotEmpty("returnBody", "{\"name\": $(fname), \"size\": $(fsize),\"hash\": $(etag)}"))
          Logger.info(s"scope = ${upToken}")
          Ok(Json.toJson(UpToken(upToken)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def pfop = Action(parse.json) {
    implicit request =>
      val auth: QiqiuAuth = createAuth
      val operater = new OperationManager(auth);
      request.body.validate[Bucket].map {
        case (b) =>
          val bucket = b.name
          val key = b.key

          val notifyURL = s"http://www.cocobabys.com/ws/notify?key=$key";
          val force = true;
          val pipeline = "";

          val params = new StringMap().putNotEmpty("notifyURL", "co")
            .putWhen("force", 1, force).putNotEmpty("pipeline", pipeline)

          var fops = "avthumb/mp4/vcodec/libx264/acodec/libfaac/stripmeta/1"
          fops += "|saveas/" + UrlSafeBase64.encodeToString(bucket + ":" + key)


          // 针对指定空间的文件触发 pfop 操作
          try {
            val id = operater.pfop(bucket, key, fops, params)
            Logger.info(s"success: $id")
          }
          catch {
            case e: QiniuException =>
              val r = e.response
              // 请求失败时简单状态信息
              Logger.info(r.toString)
          }
          Ok
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
    // 可通过下列地址查看处理状态信息。
    // 实际项目中设置 notifyURL，接受通知。通知内容和处理完成后的查看信息一致。
    //String url = "http://api.qiniu.com/status/get/prefop?id=" + id;

  }

  def pfopNotify(key: String) = Action {
    Logger.info(s"pfopNotify: $key")
    Ok( s"""{"key": $key}""")
  }
}
