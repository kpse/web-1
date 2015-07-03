angular.module('kulebaoAgent').controller 'AgentErrorCtrl',
  ['$scope', 'loggedUser',
    (scope, loggedUser) ->
      scope.adminUser = loggedUser
  ]