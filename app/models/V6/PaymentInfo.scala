package models.V6

import anorm._
import play.api.{Logger, Play}
import play.api.db.DB
import play.api.libs.json.Json
import models.helper.MD5Helper.md5
import play.api.Play.current

case class PaymentDetail(buyer_email: Option[String], transaction_id: Option[String])

case class KulebaoCustomerInfo(parent_id: Option[String], school_id: Option[Long], phone: Option[String])

case class PaymentInfo(sign: String, timestamp: Long, channel_type: String, transaction_type: String, transaction_id: String, transaction_fee: Long, messageDetail: PaymentDetail, optional: KulebaoCustomerInfo) {
  private val logger: Logger = Logger(classOf[PaymentInfo])
  def isValid = {
    //appID + appSecret + timestamp
    val apiKey = Play.current.configuration.getString("payment.ak").getOrElse("")
    val secretKey = Play.current.configuration.getString("payment.sk").getOrElse("")
    logger.debug(s"$apiKey $secretKey $timestamp sign=$sign")
    logger.debug(md5(s"$apiKey$secretKey$timestamp"))
    md5(s"$apiKey$secretKey$timestamp").equalsIgnoreCase(sign)
  }

  def save(fullContent: String) = DB.withConnection {
    implicit c =>
      SQL("insert into paymenthistory (school_id, parent_id, phone, transaction_id, transaction_type, channel_type, " +
        "transaction_fee, buyer_email, weixin_transaction_id, content, created_at) " +
        "values ({kg}, {parent_id}, {phone}, {transaction_id}, {transaction_type}, {channel_type}, {transaction_fee}, " +
        "{buyer_email}, {weixin_transaction_id}, {content}, {created_at})")
        .on(
          'kg -> optional.school_id,
          'parent_id -> optional.parent_id,
          'phone -> optional.phone,
          'transaction_id -> transaction_id,
          'transaction_type -> transaction_type,
          'channel_type -> channel_type,
          'transaction_fee -> transaction_fee,
          'buyer_email -> messageDetail.buyer_email,
          'weixin_transaction_id -> messageDetail.transaction_id,
          'content -> fullContent,
          'created_at -> timestamp
        ).executeInsert()
  }
}

object PaymentInfo {

  implicit val readPaymentDetail = Json.reads[PaymentDetail]
  implicit val readKulebaoCustomerInfo = Json.reads[KulebaoCustomerInfo]
  implicit val readPaymentInfo = Json.reads[PaymentInfo]
  implicit val writePaymentDetail = Json.writes[PaymentDetail]
  implicit val writeKulebaoCustomerInfo = Json.writes[KulebaoCustomerInfo]
  implicit val writePaymentInfo = Json.writes[PaymentInfo]

}
