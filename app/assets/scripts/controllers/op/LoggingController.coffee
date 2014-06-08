angular.module('kulebaoOp').controller 'OpLoggingMonitorCtrl',
  ['$scope', '$rootScope', '$http', '$timeout', '$location', '$anchorScroll',
    (scope, rootScope, $http, $timeout, $location, $anchorScroll) ->
      rootScope.tabName = 'logging'

      scope.gotoBottom = ->
        $location.hash('bottom');
        $anchorScroll()

      scope.content = ''

      handleCallback = (msg) ->
        scope.$apply ->
          scope.content += msg.data.replace(/\\n/g, '\n').replace(/\\\//g, '/').replace(/^'/, '').replace(/'$/, '')
        scope.gotoBottom()

      source = new EventSource '/track_logging'
      source.addEventListener 'message', handleCallback, false

  ]
