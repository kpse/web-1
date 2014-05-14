package controllers

import play.api.mvc.{Action, Controller}
import play.Logger
import scala.xml.PCData

object WeiXinController extends Controller {

  import scala.xml._

  def entry(signature: String, timestamp: Long, nonce: String, echo: String) = Action {
    Ok(echo)
  }


  def handleMessage(seq: NodeSeq) = {
    val from = seq \ "FromUserName"
    val to = seq \ "ToUserName"
    newMessage(PCData(from.text), PCData(to.text), PCData("你好，%s".format(to.text)))
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
    newMessage(PCData(from.text), PCData(from.text), PCData("谢谢点击，%s".format(to.text)))
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

}
