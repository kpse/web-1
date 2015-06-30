angular.module('kulebaoAgent').controller 'AgentSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'AdminUser',
    (scope, $rootScope, stateParams, location, User) ->
      scope.adminUser = User

  ]