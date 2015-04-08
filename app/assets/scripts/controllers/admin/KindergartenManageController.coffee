angular.module('kulebaoAdmin').controller 'KgManageCtrl',
  ['$scope', '$rootScope', '$stateParams', '$cacheFactory', '$location', '$state', 'passwordService', '$modal',
   'chargeService', '$alert', 'AdminUser', 'School', 'ClassesInSchool', 'schoolConfigService', 'classService',
   'imageCompressService', 'feedbackService',
    (scope, $rootScope, $stateParams, $cacheFactory, location, $state, Password, Modal, Charge,
     Alert, AdminUser, School, ClassesInSchool, SchoolConfig, Classes, Compress, Feedback) ->

      scope.adminUser = AdminUser
      scope.kindergarten = School
      scope.kindergarten.classes = ClassesInSchool
      scope.classesScope = scope.kindergarten.classes

      if (scope.adminUser.privilege_group isnt 'operator') && scope.adminUser.school_id != parseInt $stateParams.kindergarten
        location.path "/kindergarten/#{scope.adminUser.school_id}/welcome"

      location.path "#{location.path}/welcome" if /kindergarten\/\d+$/.test location.path()

      Charge.query school_id: $stateParams.kindergarten, (data)->
        scope.kindergarten.charge = data[0]
        if scope.kindergarten.charge.status == 0
          location.path '/expired'
      , (res) ->
        location.path "/#{res.status}"

      scope.disableMemberEditing = false
      SchoolConfig.get school_id: $stateParams.kindergarten, (data)->
        config = _.find data['config'], (item) -> item.name == 'disableMemberEditing'
        config? && scope.disableMemberEditing = config.value == 'true'

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
        $state.go 'kindergarten.employee.detail', kindergarten: $stateParams.kindergarten unless $state.includes 'kindergarten.employee', kindergarten: $stateParams.kindergarten

      scope.goBusLocation = ->
        $state.go 'kindergarten.bus', kindergarten: $stateParams.kindergarten unless $state.includes 'kindergarten.bus', kindergarten: $stateParams.kindergarten

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

      scope.isSuperUser = (user = scope.adminUser) ->
        undefined isnt _.find ['principal', 'operator'], (u) ->
          user.privilege_group == u

      scope.operatorOnly = (user) ->
        user.privilege_group == 'operator'

      scope.$on 'classUpdated', ->
        Classes.query school_id: $stateParams.kindergarten, (data) ->
          scope.kindergarten.classes = data
          scope.classesScope = data

      scope.feedback = ->
        scope.message = new Feedback
          content: ''
          timestamp: new Date().getTime()
          phone: scope.adminUser.phone
          source: 'web'
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/feedback.html'

      scope.postFeedback = (message) ->
        message.$save ->
          scope.currentModal.hide()

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

  ]