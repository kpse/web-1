angular.module('kulebaoAgent').controller 'AgentCommercialCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'AdminUser',
    (scope, $rootScope, stateParams, location, User) ->
      scope.adminUser = User


  ]