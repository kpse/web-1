angular.module('kulebaoAgent').controller 'AgentCommercialCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'currentAgent', 'loggedUser',
    (scope, $rootScope, stateParams, location, Agent, User) ->
      scope.adminUser = User


  ]