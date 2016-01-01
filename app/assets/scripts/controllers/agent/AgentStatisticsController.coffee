angular.module('kulebaoAgent').controller 'AgentStatisticsCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', 'loggedUser', 'currentAgent',
   'agentStatsService', 'agentSchoolDataService'
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, User, CurrentAgent, Stats, SchoolData) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      scope.refresh = ->
        scope.d3Data = []
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        queue = [Stats.query(agent_id: currentAgent.id).$promise,
                 scope.waitForSchoolsReady()]
        $q.all(queue).then (q) ->
          console.log currentAgent.schools
          groups = _.groupBy(q[0], 'month')
          scope.d3Data = _.map _.keys(groups), (g) ->
            once = _.sum groups[g], 'logged_once'
            ever = _.sum groups[g], 'logged_ever'
            result =
              month : g
              data : groups[g]
              loggedOnce : once
              loggedEver : ever
              rate : (if ever == 0 then 0 else once / ever * 100).toFixed 2
          scope.lastActiveData = _.last _.sortBy scope.d3Data, 'month'

          schoolDataQueue = _.map currentAgent.schools, (s) ->
            SchoolData.get(agent_id: currentAgent.id, school_id: s.school_id).$promise
          $q.all(schoolDataQueue).then (q2) ->
            scope.parentsCount = _.sum q2, 'all'
            scope.childrenCount = _.sum q2, 'children'

      scope.refresh()

  ]