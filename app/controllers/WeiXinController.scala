package controllers

import play.api.mvc.{Action, Controller}
import play.Logger
import play.api.libs.json.{Json, JsValue, JsObject}
import play.api.libs.ws.WS
import play.api.Play
import play.cache.Cache
import scala.concurrent.{ExecutionContext, Promise, Future, Await}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

object WeiXinController extends Controller {

  import scala.xml._

  def entry(signature: String, timestamp: Long, nonce: String, echo: String) = Action {
    Ok(echo)
  }


  def handleMessage(seq: NodeSeq) = {
    val from = seq \ "FromUserName"
    val to = seq \ "ToUserName"
//    val userInfo = Await.result(Await.result(queryUserInfo(to.text), 5 second), 5 seconds)
//    newMessage(PCData(from.text), PCData(to.text), PCData("你好，%s".format((userInfo \ "nickname").as[String])))
    newMessage(PCData(from.text), PCData(to.text), PCData("用户你好。"))
  }

  def newMessage(from: PCData, to: PCData, content: PCData) = <xml>
    <ToUserName>
      {from}
    </ToUserName>
    <FromUserName>
      {to}
    </FromUserName>
    <CreateTime>
      {System.currentTimeMillis}
    </CreateTime>
    <MsgType>
      {PCData("text")}
    </MsgType>
    <Content>
      {content}
    </Content>
  </xml>;

  def handleClicking(seq: NodeSeq) = {
    val from = seq \ "FromUserName"
    val to = seq \ "ToUserName"
//    val userInfo = Await.result(Await.result(queryUserInfo(to.text), 5 second), 5 seconds)
//    newMessage(PCData(from.text), PCData(to.text), PCData("谢谢点击，%s".format((userInfo \ "nickname").as[String])))
    newMessage(PCData(from.text), PCData(to.text), PCData("谢谢您的点击测试。"))
  }

  def handle = Action(parse.xml) {
    request =>
      Logger.info(request.body.toString())
      request.body match {
        case message if (request.body \ "MsgType").text.equals("text") =>
          Ok(handleMessage(request.body))
        case clicking if (request.body \ "MsgType").text.equals("event") =>
          Ok(handleClicking(request.body))
        case _ =>
          Ok("")
      }

  }

  def getToken(): Future[String] = {

    Cache.get("weixin_token") match {
      case token: String if !token.isEmpty =>
        val promise = Promise[String]()
        promise.success(token)
        promise.future
      case _ =>
        val ak = Play.current.configuration.getString("weixin.ak").getOrElse("")
        val sk = Play.current.configuration.getString("weixin.sk").getOrElse("")
        val url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s".format(ak, sk)
        Logger.info(url)
        WS.url(url).get().map {
          response =>
            val token: String = (response.json \ "access_token").as[String]
            Cache.set("weixin_token", token, 7200)
            token
        }
    }
  }

  def queryUserInfo(openId: String) = {
    getToken().map {
      token =>
        val url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s".format(token, openId)
        Logger.info(url)
        WS.url(url).get().map {
          response =>
            response.json
        }
    }
  }

}
