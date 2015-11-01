package controllers

import java.net.URLEncoder

import com.qiniu.util.{Auth => QiqiuAuth, StringMap}
import controllers.helper.QiniuHelper
import models.Bucket.writesUpToken
import models.PfopNotification.readPfopNotification
import models.{Bucket, BucketInfo, News, PfopNotification, UpToken}
import models.Bucket.writesBucketInfo
import play.api.Logger
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


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

  val defaultBucket = QiniuHelper.defaultBucket

  def generateToken(bucket: Option[String], key: Option[String]) = Action {
    val auth: QiqiuAuth = QiniuHelper.createAuth
    val upToken = auth.uploadToken(bucket.getOrElse(defaultBucket), key.orNull, 3600, new StringMap().putNotEmpty("returnBody", "{\"name\": $(fname), \"size\": $(fsize),\"hash\": $(etag)}"))
    Ok(Json.toJson(UpToken(upToken)))
  }

  def generateEncodeToken(bucket: Option[String], key: Option[String]) = key match {
    case Some(s) =>
      val encodedKey = URLEncoder.encode(s, "UTF-8")
      Logger.info("key = %s".format(encodedKey))
      generateToken(bucket, Some(encodedKey))
    case None =>
      generateToken(bucket, None)
  }

  def generateSafeToken = Action(parse.json) {
    implicit request =>
      request.body.validate[Bucket].map {
        case (bucket) =>
          val auth: QiqiuAuth = QiniuHelper.createAuth
          val bucketName: String = bucket.name.getOrElse(defaultBucket)
          Logger.info(s"bucketName = $bucketName")
          val upToken = auth.uploadToken(bucketName, bucket.key, 3600, new StringMap().putNotEmpty("returnBody", "{\"name\": $(fname), \"size\": $(fsize),\"hash\": $(etag)}"))
          Logger.info(s"scope = ${upToken}")
          Ok(Json.toJson(UpToken(upToken)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def pfop = Action(parse.json) {
    implicit request =>
      request.body.validate[Bucket].map {
        case (b) =>
          QiniuHelper.triggerPfop(b)
          Ok
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
    // 可通过下列地址查看处理状态信息。
    // 实际项目中设置 notifyURL，接受通知。通知内容和处理完成后的查看信息一致。
    //String url = "http://api.qiniu.com/status/get/prefop?id=" + id

  }

  def pfopNotify(key: String) = Action(parse.json) {

    implicit request =>
      request.body.validate[PfopNotification].map {
        case (p) =>
          Logger.info(s"pfopNotify: $p for key $key")
          p.save(key)
          Ok( s"""{"key": $key}""")
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def defaultPostBucket() = Action {
    val defaultBucket = QiniuHelper.defaultBucket
    val defaultBucketUrl = QiniuHelper.defaultBucketUrl
    Ok(Json.toJson(BucketInfo(defaultBucket, defaultBucketUrl)))
  }
}
