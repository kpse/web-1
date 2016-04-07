'use strict'

angular.module('kulebaoApp')
.controller('ForgottenCtrl', [
    '$scope', '$rootScope', '$stateParams', '$location', '$http', '$timeout', 'employeePhoneService', '$alert',
    'passwordTokenService',
  (scope, rootScope, stateParams, location, $http, $timeout, Phone, Alert, Token) ->
    scope.secondsRemaining = 0
    countDown = ->
      $timeout ->
        if scope.secondsRemaining > 0
          scope.secondsRemaining--
          countDown()
      , 1000
    scope.sendToken = (phone) ->
      Phone.get phone: phone, (data) ->
          scope.employee = data
          Token.bind(phone: phone).get ->
              Alert
                title: '验证码已发送。'
                content: '请注意查看手机短信。'
                placement: "top"
                type: "info"
                container: '.phone-input-panel'
                duration: 3
              scope.secondsRemaining = 120
              countDown()
            , (err) ->
              delete scope.employee
              alertError err.data, '请求发送短信错误。'
        , (err) ->
          delete scope.employee
          alertError err.data, '号码不存在'


    scope.validate = (token) ->
      result = Token.save phone: scope.employee.phone, code: token, ->
          if result.error_code == 0
            rootScope.resetToken = token
            location.path "/reset/#{scope.employee.phone}"
          else
            alertError(result)
        , (res) ->
          alertError(res.data)

    alertError = (data, title = '验证码错误') ->
      Alert
        title: title
        content: data.error_msg
        placement: "top-left"
        type: "danger"
        container: '.phone-input-panel'
        duration: 3

])
.controller 'PasswordCtrl', [ '$scope', '$rootScope', '$stateParams',
                              '$location', '$http', '$alert', 'employeePhoneService', '$window',
  (scope, rootScope, stateParams, location, $http, Alert, ResetPassword, $window) ->
    location.path '/password' if rootScope.resetToken is undefined
    scope.user = ResetPassword.get phone: stateParams.phone

    scope.reset = (user) ->
      user.old_password = ''
      user.token = rootScope.resetToken
      ResetPassword.save user, ->
          delete rootScope.resetToken
          $window.location.href = '/newlogin'
        , (res) ->
          handleValidateError(res.data)

    handleValidateError = (data) ->
      Alert
        title: '重置密码失败'
        content: '请检查你的验证码'
        placement: "top-left"
        type: "danger"
        container: '.reset-password-panel'
        duration: 3

]
