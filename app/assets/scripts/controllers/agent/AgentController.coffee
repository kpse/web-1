angular.module('kulebaoAgent').controller 'AgentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', 'loggedUser',
    (scope, $rootScope, $stateParams, $state, User) ->
      scope.loggedUser = User
      console.log(scope.loggedUser)

      $state.go('main', agent_id: scope.loggedUser.id) if $stateParams.agent_id == 'default'
      $state.go('main', agent_id: scope.loggedUser.id) unless "#{scope.loggedUser.id}".indexOf('_') > 0

  ]