angular.module('kulebaoOp').controller 'OpLoggingMonitorCtrl',
  ['$scope', '$rootScope', '$http',
    (scope, rootScope, $http) ->
      rootScope.tabName = 'logging'

      $http(method: 'GET', url: '/api/v1/logging').
        success((data) ->
          scope.content = data
        )

  ]
