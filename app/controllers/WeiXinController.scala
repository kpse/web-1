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
    newMessage(PCData(to.text), PCData(from.text), PCData("用户你好。"))
  }

  def newMessage(from: PCData, to: PCData, content: PCData) = <xml>
    <ToUserName>
      {to}
    </ToUserName>
    <FromUserName>
      {from}
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

  def IntroMessage(from: PCData, to: PCData) = <xml>
    <ToUserName>
      {to}
    </ToUserName>
    <FromUserName>
      {from}
    </FromUserName>
    <CreateTime>
      {System.currentTimeMillis}
    </CreateTime>
    <MsgType>
      {PCData("news")}
    </MsgType>
    <ArticleCount>2</ArticleCount>
    <Articles>
      <item>
        <Title>
          {PCData("我们是幼乐宝")}
        </Title>
        <Description>
          {PCData("成都库乐宝信息技术有限公司位于成都市高新区天府软件园旁，是一家专业从事幼教行业移动互联网应用的高新技术企业，拥有多项自主知识产权及产品，行业经验及技术能力均处于国内领先地位。")}
        </Description>
        <PicUrl>
          {PCData("http://kulebao-prod.qiniudn.com/11.jpg")}
        </PicUrl>
        <Url>
          {PCData("http://www.cocobabys.com")}
        </Url>
      </item>
      <item>
        <Title>
          {PCData("幼乐宝的重要性")}
        </Title>
        <Description>
          {PCData(
            """
              |对于家长：
              |将孩子送到幼儿园，宝宝日常的衣食住行都是每位父母担心的问题。
              |宝宝的安全更是为人父母关注的重点，宝宝的安全出入园信息如何有效反馈。
              |宝宝入园之后会不会被别有用心的人冒名领走？年迈的爷爷奶奶有没有及时接送宝宝？
              |宝宝每日在幼儿园开不开心、安不安全？一大堆的问题都是家长希望及时知道的。
              |原来我们只能采用听老师口述、收发短信、拨打电话等费时费力高成本的沟通方式，既不能满足现代家长快节奏工作的需要，又浪费了大量金钱和时间。
              |现在，家长们只需要动动手指就能解决这一切问题。
            """.stripMargin)}
        </Description>
        <PicUrl>
          {PCData("http://kulebao-prod.qiniudn.com/logo_200.png")}
        </PicUrl>
        <Url>
          {PCData("http://www.cocobabys.com/app_package/download")}
        </Url>
      </item>
    </Articles>
  </xml>;

  def handleClicking(seq: NodeSeq) = {
    val from = seq \ "FromUserName"
    val to = seq \ "ToUserName"
    //    val userInfo = Await.result(Await.result(queryUserInfo(to.text), 5 second), 5 seconds)
    //    newMessage(PCData(from.text), PCData(to.text), PCData("谢谢点击，%s".format((userInfo \ "nickname").as[String])))
    val key = (seq \ "EventKey").text
    key match {
      case "V1001_INTRO" =>
        IntroMessage(PCData(to.text), PCData(from.text))
      case "V1001_GOOD" =>
        newMessage(PCData(to.text), PCData(from.text), PCData("谢谢你的赞，我们会继续努力的。"))
      case _ =>
        newMessage(PCData(to.text), PCData(from.text), PCData("谢谢您的点击测试。"))
    }
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

  def getToken: Future[String] = {

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
    getToken.map {
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
