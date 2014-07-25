angular.module('kulebaoAdmin').controller 'KgManageCtrl',
  ['$scope', '$rootScope', '$stateParams', '$cacheFactory', 'schoolService', '$location', 'employeeService', 'passwordService', '$modal',
   'chargeService', 'uploadService', '$alert', 'StatsService',
    (scope, $rootScope, $stateParams, $cacheFactory, School, location, Employee, Password, Modal, Charge, Upload, Alert, Stats) ->
      scope.adminUser = Employee.get ->
        if (scope.adminUser.privilege_group isnt 'operator') && scope.adminUser.school_id != parseInt $stateParams.kindergarten
          location.path "/kindergarten/#{scope.adminUser.school_id}/welcome"

        scope.adminUser.assignment = Stats('assignment').query school_id: $stateParams.kindergarten, employee_id: scope.adminUser.id
        scope.adminUser.assess = Stats('assess').query school_id: $stateParams.kindergarten, employee_id: scope.adminUser.id
        scope.adminUser.conversation = Stats('conversation').query school_id: $stateParams.kindergarten, employee_id: scope.adminUser.id
        scope.adminUser.news = Stats('news').query school_id: $stateParams.kindergarten, employee_id: scope.adminUser.id

        scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
          Charge.query school_id: $stateParams.kindergarten, (data)->
            scope.kindergarten.charge = data[0]
            if scope.kindergarten.charge.status == 0
              location.path '/expired'
        , (res) ->
          location.path "/#{res.status}"

      scope.isSelected = (tab)->
        tab is $rootScope.tabName

      goPageWithClassesTab = (pageName, subName)->
        if location.path().indexOf("#{pageName}/class") < 0
          location.path "/kindergarten/#{$stateParams.kindergarten}/#{pageName}"
        else if subName? && location.path().indexOf("/#{subName}/") > 0
          location.path location.path().replace new RegExp("/#{subName}/.+$", "g"), '/list'
        else
          location.path location.path().replace /\/[^\/]+$/, '/list'

      scope.goBulletin = ->
        goPageWithClassesTab 'bulletin'

      scope.goConversation = ->
        goPageWithClassesTab 'conversation', 'child'

      scope.goAssignment = ->
        goPageWithClassesTab 'assignment'

      scope.goBabyStatus = ->
        goPageWithClassesTab 'baby-status', 'child'

      scope.goMemberList = ->
        goPageWithClassesTab 'member'

      scope.goDailyLog = ->
        goPageWithClassesTab 'dailylog', 'child'

      scope.goRelationshipManagement = ->
        goPageWithClassesTab 'relationship/type/connected'

      scope.goSchedule = ->
        goPageWithClassesTab 'schedule'

      scope.goHistory = ->
        goPageWithClassesTab 'history', 'child'

      scope.goEmployeeList = ->
        location.path "/kindergarten/#{$stateParams.kindergarten}/employee/detail"


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
          $cacheFactory.get('$http').removeAll()
          scope.adminUser = user
          scope.currentModal.hide()

      scope.showingUp = (user) ->
        undefined isnt _.find ['principal', 'operator'], (u) ->
          user.privilege_group == u

      scope.operatorOnly = (user) ->
        user.privilege_group == 'operator'

  ]