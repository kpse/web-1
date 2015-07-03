angular.module('kulebaoAgent').controller 'AgentErrorCtrl',
  ['$scope', 'employeeService',
    (scope, loggedUser) ->
      scope.adminUser = loggedUser.get()
  ]