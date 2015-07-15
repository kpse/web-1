angular.module('kulebaoAgent').controller 'AgentStatisticsCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter','loggedUser', 'currentAgent',
   'agentSchoolService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, User, CurrentAgent, AgentSchool) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      scope.d3Data = [{month: '201412', rate: 100}, {month: '201502', rate: 88.65}, {month: '201501', rate: 56.88}, {month: '201503', rate: 12.02} ]

  ]