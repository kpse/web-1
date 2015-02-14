angular.module('kulebaoOp').controller 'OpPrincipalCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', 'allEmployeesService', '$modal',
    (scope, rootScope, School, Clazz, Employee, Modal) ->
      rootScope.tabName = 'principal'

      displayRole = (employee) ->
        switch employee.privilege_group
          when "operator" then {role: '超级管理员', rank: 100}
          when "principal" then {role: '校长', rank: 50}
          when "teacher" then {role: '老师', rank: 10}
          else {role: group, rank: -1}

      scope.refresh = ->
        scope.loading = true
        scope.employees = Employee.query ->
          _.forEach scope.employees, (e) -> e.rank = displayRole(e)
          scope.loading = false

      scope.refresh()

      scope.edit = (employee) ->
        scope.employee = angular.copy employee
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.delete = (employee) ->
        employee.$delete ->
          scope.refresh()

      scope.save = (object) ->
        object.$save ->
          scope.refresh()
          scope.currentModal.hide()

  ]

