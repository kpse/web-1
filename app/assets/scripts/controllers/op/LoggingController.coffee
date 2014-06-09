angular.module('kulebaoOp').controller 'OpLoggingMonitorCtrl',
  ['$scope', '$rootScope', '$http', '$timeout', '$location', '$anchorScroll', 'eventStreamService',
    (scope, rootScope, $http, $timeout, $location, $anchorScroll, Event) ->
      rootScope.tabName = 'logging'

      scope.gotoBottom = ->
        $location.hash('bottom');
        $anchorScroll()

      scope.content = ''

      handleCallback = (msg) ->
        scope.$apply ->
          scope.content += msg.data.replace(/\\n/g, '\n').replace(/\\\//g, '/').replace(/^'/, '').replace(/'$/, '')
        scope.gotoBottom()

      Event.init('/track_logging', handleCallback)

  ]
