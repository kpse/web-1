angular.module('kulebaoAgent').controller 'AgentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'currentAgent', 'loggedUser',
    (scope, $rootScope, stateParams, location, Agent, User) ->
      scope.currentAgent = Agent
      scope.loggedUser = User
  ]