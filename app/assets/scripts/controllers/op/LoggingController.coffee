angular.module('kulebaoOp').controller 'OpLoggingMonitorCtrl',
  ['$scope', '$rootScope', '$http', '$timeout', '$location', '$anchorScroll',
    (scope, rootScope, $http, $timeout, $location, $anchorScroll) ->
      rootScope.tabName = 'logging'

      scope.gotoBottom = ->
        $location.hash('bottom');
        $anchorScroll()

      scope.poll = ->
        $http(method: 'GET', url: '/heartbeat').
          success((data) ->
            scope.content = data
            scope.gotoBottom()
            $timeout(scope.poll, 9000, true) if rootScope.tabName is 'logging'
          )
        .error(->
            $timeout(scope.poll, 9000, true) if rootScope.tabName is 'logging'
        )

      scope.poll()

  ]
