angular.module('kulebaoAgent').controller 'AgentSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'currentAgent', 'loggedUser'
    (scope, $rootScope, stateParams, location, Agent, User) ->
      scope.loggedUser = User
      scope.currentAgent = Agent
      scope.$emit 'currentAgent', scope.currentAgent

  ]