angular.module('kulebaoAdmin').controller 'KgManageCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$location', 'employeeService', 'passwordService', '$modal',
   'chargeService', 'uploadService', '$alert',
    (scope, $rootScope, $stateParams, School, location, Employee, Password, Modal, Charge, Upload, Alert) ->
      scope.adminUser = Employee.get ->
        if (scope.adminUser.privilege_group isnt 'operator')
          location.path '/kindergarten/' + scope.adminUser.school_id + '/welcome'

        scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
          scope.kindergarten.charge = Charge.query school_id: $stateParams.kindergarten, ->
            if scope.kindergarten.charge[0] && scope.kindergarten.charge[0].status == 0
              location.path '/expired'
        , (res) ->
          location.path '/' + res.status

      scope.isSelected = (tab)->
        tab is $rootScope.tabName

      goPageWithClassesTab = (pageName, subName)->
        if location.path().indexOf(pageName + '/class') < 0
          location.path '/kindergarten/' + $stateParams.kindergarten + '/' + pageName
        else if subName? && location.path().indexOf('/' + subName + '/') > 0
          location.path location.path().replace(new RegExp('/' + subName + '/.+$',"g"), '/list')
        else
          location.path location.path().replace(/\/[^\/]+$/, '/list')

      scope.goBulletin = ->
        goPageWithClassesTab 'bulletin'

      scope.goConversation = ->
        goPageWithClassesTab 'conversation', 'card'

      scope.goAssignment = ->
        goPageWithClassesTab 'assignment'

      scope.goBabyStatus = ->
        goPageWithClassesTab 'baby-status', 'child'

      scope.goMemberList = ->
        goPageWithClassesTab 'member'

      scope.goRelationshipManagement = ->
        goPageWithClassesTab 'relationship/type/connected'

      scope.goSchedule = ->
        goPageWithClassesTab 'schedule'

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
          scope.adminUser = Employee.get()
          scope.currentModal.hide()

      scope.uploadPic = (employee, pic) ->
        scope.uploading = true
        Upload pic, (url) ->
          scope.$apply ->
            employee.portrait = url if url isnt undefined
            scope.uploading = false
        , scope.adminUser.id
  ]