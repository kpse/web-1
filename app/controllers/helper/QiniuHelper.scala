package controllers.helper

import com.qiniu.common.QiniuException
import com.qiniu.processing.OperationManager
import com.qiniu.util.{Auth => QiqiuAuth, UrlSafeBase64, StringMap}
import models.Bucket
import play.api.{Logger, Play}

object QiniuHelper {
  def createAuth = {
    val ACCESS_KEY = Play.current.configuration.getString("oss.ak").getOrElse("")
    val SECRET_KEY = Play.current.configuration.getString("oss.sk").getOrElse("")
    Logger.info("ACCESS_KEY = %s, SECRET_KEY = %s".format(ACCESS_KEY, SECRET_KEY))
    QiqiuAuth.create(ACCESS_KEY, SECRET_KEY)
  }

  def triggerPfop(b: Bucket) = {
    val auth: QiqiuAuth = createAuth
    val operator = new OperationManager(auth)
    val bucket = b.name
    val key = b.key

    val saveAs: String = UrlSafeBase64.encodeToString(bucket + ":" + key)
    Logger.info(s"saveAs: $saveAs")
    val notifyURL = s"https://stage2.cocobabys.com/ws/notify_prefop?key=$saveAs"
    val force = true
    val pipeline = ""

    val params = new StringMap().putNotEmpty("notifyURL", notifyURL)
      .putWhen("force", 1, force).putNotEmpty("pipeline", pipeline)

    val fops = "avthumb/mp4/vcodec/libx264/acodec/libfaac/stripmeta/1" + "|saveas/" + saveAs

    // 针对指定空间的文件触发 pfop 操作
    try {
      val id = operator.pfop(bucket, key, fops, params)
      Logger.info(s"operator.pfop success: $id")
    }
    catch {
      case e: QiniuException =>
        val r = e.response
        // 请求失败时简单状态信息
        Logger.info(s"operator.pfop failed: $r")
    }
  }
}
