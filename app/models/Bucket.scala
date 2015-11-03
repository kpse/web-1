package models

import java.net.URI
import controllers.helper.QiniuHelper
import play.api.Play
import play.api.libs.json.Json

case class UpToken(token: String)

case class BucketInfo(name: String, urlPrefix: String)
case class Bucket(name: Option[String], key: String) {
  val defaultBucket = QiniuHelper.defaultBucket
  def scope = s"${name.getOrElse(defaultBucket)}:$key"
}

object Bucket {
  implicit val readBucket = Json.reads[Bucket]
  implicit val writesUpToken = Json.writes[UpToken]
  implicit val writesBucketInfo = Json.writes[BucketInfo]

  def parse(url: String): Option[Bucket] = {
    val bucketsMap = Map("dn-cocobabys-test.qbox.me" -> "cocobabys-test", "dn-cocobabys.qbox.me" -> "cocobabys",
      "dn-kulebao.qbox.me" -> "kulebao-prod", "dn-youlebao.qbox.me" -> "kulebao-prod", "suoqin-test.u.qiniudn.com" -> "suoqin-test",
    "dn-local-test.qbox.me" -> "localtest")
    val uri: URI = new URI(url)
    bucketsMap.get(uri.getHost) map {
      case name =>
        Bucket(Some(name), uri.getRawPath.replaceFirst("^/", ""))
    }

  }
}
