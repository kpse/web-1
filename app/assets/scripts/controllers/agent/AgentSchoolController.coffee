angular.module('kulebaoAgent').controller 'AgentSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'currentAgent', 'loggedUser'
    (scope, $rootScope, stateParams, location, Agent, User) ->
      scope.adminUser = User
      scope.currentAgent = Agent

  ]