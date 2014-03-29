angular.module('kulebaoOp').controller 'OpVerificationCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', 'allEmployeesService',
    (scope, rootScope, School, Clazz, Employee) ->
      rootScope.tabName = 'code'

      scope.loading = true
      scope.employees = Employee.query ->
        scope.loading = false


  ]

