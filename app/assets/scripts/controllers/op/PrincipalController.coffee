angular.module('kulebaoOp').controller 'OpPrincipalCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', 'allEmployeesService', '$modal',
    (scope, rootScope, School, Clazz, Employee, Modal) ->
      rootScope.tabName = 'principal'
      scope.levels = ['校长','老师', '超级管理员']
      scope.current_employee_role = scope.levels[0]

      scope.display = (role) ->
        if role != scope.current_employee_role
          scope.current_employee_role = role
          scope.refresh()



      displayRole = (employee) ->
        switch employee.privilege_group
          when "operator" then {role: '超级管理员', rank: 100}
          when "principal" then {role: '校长', rank: 50}
          when "teacher" then {role: '老师', rank: 10}
          else {role: group, rank: -1}

      scope.refresh = ->
        rootScope.loading = true
        scope.employees = Employee.query ->
          _.each scope.employees, (e) -> e.rank = displayRole(e)
          rootScope.loading = false

      scope.display('校长')

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

