angular.module('kulebaoAgent').controller 'AgentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', 'loggedUser',
    (scope, $rootScope, $stateParams, $state, $location, User) ->
      scope.loggedUser = User
      console.log(scope.loggedUser)
      console.log($stateParams)

      if $stateParams.agent_id == 'default' || "#{scope.loggedUser.id}".indexOf('_') < 0
        $location.path "main/#{scope.loggedUser.id}/school"

      scope.$on 'currentAgent', (e, agent) ->
        scope.currentAgent = agent
        console.log scope.currentAgent

  ]