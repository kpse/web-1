angular.module('kulebaoOp').controller 'OpCtrl',
  ['$scope', '$rootScope', '$stateParams', 'employeeService', 'passwordService', '$modal', '$alert', 'uploadService',
   '$location',
    (scope, $rootScope, stateParams, Employee, Password, Modal, Alert, Upload, location) ->
      scope.adminUser = Employee.get()

      scope.isSelected = (tab)->
        tab == $rootScope.tabName

      scope.changePassword = (user) ->
        scope.user = angular.copy user
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/change_password.html'

      scope.change = (user) ->
        pw = new Password
          school_id: user.school_id
          phone: user.phone
          employee_id: user.id
          login_name: user.login_name
          old_password: user.old_password
          new_password: user.new_password
        pw.$save ->
          Alert
            title: '密码修改成功'
            content: ''
            type: 'success'
          scope.currentModal.hide()
        , (res) ->
          Alert
            title: '密码修改失败'
            content: res.data.error_msg
            container: '.change-password-form'


      scope.edit = (user) ->
        scope.employee = angular.copy user
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.save = (user) ->
        user.$save ->
          scope.adminUser = user
          scope.currentModal.hide()

      scope.uploadPic = (employee, pic) ->
        scope.uploading = true
        Upload pic, scope.adminUser.id, (url) ->
          scope.$apply ->
            employee.portrait = url if url isnt undefined
            scope.uploading = false

      goPageWithTab = (pageName, tabName, defaultValue)->
        if location.path().indexOf(pageName + '/' + tabName) < 0
          location.path '/main/' + pageName + '/' + tabName + '/' + defaultValue

      scope.goFeedback = ->
        goPageWithTab 'feedback', 'source', 'android_parent/list'

      scope.goAppRelease = ->
        goPageWithTab 'app', 'type', 'parent/detail'
  ]