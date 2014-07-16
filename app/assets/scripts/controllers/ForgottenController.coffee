'use strict'

angular.module('kulebaoApp')
.controller('ForgottenCtrl', [ '$scope', '$rootScope', '$stateParams',
                               '$location', '$http', 'employeePhoneService', '$alert', 'passwordTokenService',
  (scope, rootScope, stateParams, location, $http, Phone, Alert, Token) ->
    scope.sendToken = (phone) ->
      scope.employee = Phone.get phone: phone, ->
        Token.bind(phone: phone).get ->
          Alert
            title: '验证码已发送。'
            content: '请注意查看手机短信。'
            placement: "top"
            type: "info"
            container: '.phone-input-panel'
            duration: 3
      , (res) ->
        delete scope.employee
        Alert
          title: '号码不存在。'
          content: res.data.error_msg
          placement: "top-left"
          type: "danger"
          container: '.phone-input-panel'
          duration: 3

    scope.validate = (token) ->
      result = Token.save phone: scope.employee.phone, code: token, ->
        if result.error_code == 0
          rootScope.resetToken = token
          location.path('/reset/' + scope.employee.phone)
        else
          handleValidateError(result)
      , (res) ->
        handleValidateError(res.data)

    handleValidateError = (data) ->
      Alert
        title: '验证码错误'
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
        $window.location.href = '/login'
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
