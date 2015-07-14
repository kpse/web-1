angular.module('kulebaoAgent').controller 'AgentStatisticsCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter','loggedUser', 'currentAgent',
   'agentSchoolService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, User, CurrentAgent, AgentSchool) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      scope.d3Data = [{date: '201412', count: 100}, {date: '201502', count: 88.65}, {date: '201501', count: 56.88}, {date: '201503', count: 12.02} ]

  ]