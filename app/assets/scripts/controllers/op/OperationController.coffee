angular.module('kulebaoOp').controller 'OpCtrl',
  ['$scope', '$rootScope', '$stateParams', 'employeeService', 'passwordService', '$modal',
    (scope, $rootScope, stateParams, Employee, Password, Modal) ->
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
          scope.currentModal.hide()

      scope.edit = (user) ->
        scope.employee = angular.copy user
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.save = (user) ->
        user.$save ->
          scope.adminUser = Employee.get()
          scope.currentModal.hide()
  ]