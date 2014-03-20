angular.module('kulebaoAdmin').controller 'KgManageCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$location', 'employeeService', 'passwordService', '$modal',
   'chargeService',
    (scope, $rootScope, $stateParams, School, location, Employee, Password, Modal, Charge) ->
      scope.adminUser = Employee.get ->
        if (scope.adminUser.school_id != parseInt $stateParams.kindergarten) &&
            (scope.adminUser.privilege_group != 'operator')
          location.path '/kindergarten/' + scope.adminUser.school_id
        scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
          scope.kindergarten.charge = Charge.query school_id: $stateParams.kindergarten, ->
            if scope.kindergarten.charge[0] && scope.kindergarten.charge[0].status == 0
              location.path '/expired'

      scope.isSelected = (tab)->
        tab is $rootScope.tabName

      goPageWithClassesTab = (pageName)->
        if location.path().indexOf(pageName + '/class') < 0
          location.path('/kindergarten/' + $stateParams.kindergarten + '/' + pageName)
        else
          location.path(location.path().replace(/\/[^\/]+$/, '/list'))

      scope.goConversation = ->
        goPageWithClassesTab('conversation')

      scope.goAssignment = ->
        goPageWithClassesTab('assignment')

      scope.goBabyStatus = ->
        goPageWithClassesTab('baby-status')

      scope.goMemberList = ->
        goPageWithClassesTab('member')

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
          scope.currentModal.hide()
  ]