package models

import java.net.URI
import play.api.libs.json.Json

case class UpToken(token: String)

case class Bucket(name: String, key: String) {
  def scope = s"$name:$key"
}

object Bucket {
  implicit val readBucket = Json.reads[Bucket]
  implicit val writesUpToken = Json.writes[UpToken]

  def parse(url: String): Option[Bucket] = {
    val bucketsMap = Map("dn-cocobabys-test.qbox.me" -> "cocobabys-test", "dn-cocobabys.qbox.me" -> "cocobabys",
      "dn-kulebao.qbox.me" -> "kulebao-prod", "dn-youlebao.qbox.me" -> "kulebao-prod", "suoqin-test.u.qiniudn.com" -> "suoqin-test")
    val uri: URI = new URI(url)
    bucketsMap.get(uri.getHost) map {
      case name =>
        Bucket(name, uri.getRawPath.replaceFirst("^/", ""))
    }

  }
}
