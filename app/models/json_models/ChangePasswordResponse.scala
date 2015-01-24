package models.json_models

import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.Logger
import models.helper.MD5Helper.md5
import play.cache.Cache
import models.CheatCode.validate
import models.Employee

case class ChangePassword(account_name: String, old_password: String, new_password: String)

case class ChangePasswordResponse(error_code: Int, access_token: String)

case class ResetPassword(account_name: String, authcode: String, new_password: String)

object ChangePasswordResponse {
  def handleEmployeeReset(request: ResetPassword) = DB.withConnection {
    implicit c =>
      val phone = Employee.getPhoneByLoginName(request.account_name)
      request.authcode match {
        case code if isValidCode(phone, request.authcode) =>
          val updateTime = System.currentTimeMillis
          SQL("update employeeinfo set login_password={new_password}, update_at={timestamp} where login_name={username} and status=1")
            .on('username -> request.account_name,
              'new_password -> md5(request.new_password),
              'timestamp -> updateTime
            ).executeUpdate
          cleanCache(phone)
          ChangePasswordResponse(0, updateTime.toString)
        case error => ChangePasswordResponse(1232, "验证码错误(token is invalid)。")
      }
  }


  def cleanCache(phone: String) = Cache.remove(phone)

  def handleReset(request: ResetPassword) = DB.withConnection {
    implicit c =>
      request.authcode match {
        case code if isValidCode(request) =>
          val updateTime = System.currentTimeMillis
          SQL("update accountinfo set password={new_password}, pwd_change_time={timestamp} where accountid={username}")
            .on('username -> request.account_name,
              'new_password -> md5(request.new_password),
              'timestamp -> updateTime
            ).executeUpdate
          cleanCache(request.account_name)
          ChangePasswordResponse(0, updateTime.toString)
        case error => ChangePasswordResponse(1232, "验证码错误(token is invalid)。")
      }
  }

  def isValidCode(code: ResetPassword): Boolean = {
    code.authcode.equals(Cache.get(code.account_name)) || validate(code.authcode)
  }

  def isValidCode(account_name: String, authcode: String): Boolean = {
    authcode.equals(Cache.get(account_name)) || validate(authcode)
  }

  def handle(request: ChangePassword) = DB.withConnection {
    implicit c =>
      val firstRow = SQL("select * from accountinfo where accountid={username} and password={password}")
        .on('username -> request.account_name,
          'password -> md5(request.old_password)
        ).apply()
      Logger.info(firstRow.toString())
      firstRow.isEmpty match {
        case false =>
          val updateTime = System.currentTimeMillis
          SQL("update accountinfo set password={new_password}, pwd_change_time={timestamp} where accountid={username} and password={password}")
            .on('username -> request.account_name,
              'password -> md5(request.old_password),
              'new_password -> md5(request.new_password),
              'timestamp -> updateTime
            ).executeUpdate
          ChangePasswordResponse(0, updateTime.toString)
        case true =>
          ChangePasswordResponse(1, "")
      }
  }
}


