package models.helper

import models.helper.MD5Helper._

object PasswordHelper {
  def ErrorMessage = "新密码应为6位到16位数字+字母。"

  def isValid(password: String): Boolean = password.matches("^\\w{6,16}$")

  def generateNewPassword(number: String) = md5(number.drop(3))
}
