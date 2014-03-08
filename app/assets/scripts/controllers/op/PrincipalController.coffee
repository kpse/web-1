angular.module('kulebaoOp').controller 'OpPrincipalCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', 'allEmployeesService',
    (scope, rootScope, School, Clazz, Employee) ->
      rootScope.tabName = 'principal'

      scope.loading = true
      scope.employees = Employee.query ->
        scope.loading = false


  ]

