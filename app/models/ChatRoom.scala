package models

import akka.actor._
import scala.concurrent.duration._
import scala.language.postfixOps

import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsArray
import play.api.libs.json.JsString
import play.api.libs.json.JsObject

object Robot {

  def apply(chatRoom: ActorRef): Cancellable = {

    // Create an Iteratee that logs all messages to the console.
    val loggerIteratee = Iteratee.foreach[JsValue](event => Logger("robot").info(event.toString()))

    implicit val timeout = Timeout(1 second)
    // Make the robot join the room
    chatRoom ? Join("Robot") map {
      case Connected(robotChannel) =>
        // Apply this Enumerator on the logger.
        robotChannel |>> loggerIteratee
    }

    // Make the robot talk every 30 seconds
    Akka.system.scheduler.schedule(
      30 seconds,
      1 minute,
      chatRoom,
      Talk("Robot", "有人在么，怎么大家都不说话？")
    )
  }

}

object ChatRoom {

  implicit val timeout = Timeout(1 second)
  lazy val room = {
    val roomActor = Akka.system.actorOf(Props[ChatRoom])

    // Create a bot user (just for fun)
    val robot = Robot(roomActor)

    (roomActor, robot)
  }

  def join(username: String): scala.concurrent.Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {
    val (default, robot) = room
    var robotRef = robot
    (default ? Join(username)).map {

      case Connected(enumerator) =>

        // Create an Iteratee to consume the feed
        val iteratee = Iteratee.foreach[JsValue] { event =>
          Logger.info(event.toString())
          val text: String = (event \ "text").as[String]
          val Ban = "\\*ban (\\w+)".r
          val Add = "\\*add (\\w+)".r
          text match {
            case Ban(user) =>
              default ! Quit(user)
              robotRef.cancel()
            case Add("Robot") | Add("robot") =>
              robotRef = Robot(default)
            case _ =>
              default ! Talk(username, text)
          }

        }.map { _ =>
          Logger.info(username)
          default ! Quit(username)
        }

        (iteratee, enumerator)

      case CannotConnect(error) =>

        // Connection error

        // A finished Iteratee sending EOF
        val iteratee = Done[JsValue, Unit]((), Input.EOF)

        // Send an error and close the socket
        val enumerator = Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))

        (iteratee, enumerator)

    }

  }

}

class ChatRoom extends Actor {

  var members = Set.empty[String]
  val (chatEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  def receive = {

    case Join(username) =>
      if (members.contains(username)) {
        sender ! CannotConnect("这个用户名已存在。")
      } else {
        members = members + username
        sender ! Connected(chatEnumerator)
        self ! NotifyJoin(username)
      }

    case NotifyJoin(username) =>
      notifyAll("join", username, s"${username}进入聊天室。")

    case Talk(username, text) =>
      text.startsWith("*") match {
        case true =>
          notifyAll("action", username, username + text.replaceFirst("^\\*", ""))
        case false =>
          notifyAll("talk", username, text)
      }

    case Quit(username) =>
      members = members - username
      notifyAll("quit", username, s"${username}离开了聊天室。")

  }

  def notifyAll(kind: String, user: String, text: String) {
    val msg = JsObject(
      Seq(
        "kind" -> JsString(kind),
        "user" -> JsString(user),
        "message" -> JsString(text),
        "members" -> JsArray(
          members.toList.map(JsString)
        )
      )
    )
    chatChannel.push(msg)
  }

}

case class Join(username: String)

case class Quit(username: String)

case class Talk(username: String, text: String)

case class NotifyJoin(username: String)

case class Connected(enumerator: Enumerator[JsValue])

case class CannotConnect(msg: String)
