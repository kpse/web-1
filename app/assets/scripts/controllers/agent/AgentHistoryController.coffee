angular.module('kulebaoAgent')
.controller 'AgentHistoryCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', 'loggedUser', 'currentAgent',
   'agentSchoolService', 'agentStatsService', 'agentSchoolDataService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, User, CurrentAgent, AgentSchool, Stats, SchoolData) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      scope.pastMonths = [1..12].map (d) ->
        a = new Date()
        a.setDate(1)
        a.setMonth(a.getMonth() - d)
        a.getFullYear() + ('0' + (a.getMonth() + 1)).slice(-2) + ""

      scope.currentMonth = _.first scope.pastMonths
      scope.currentMonthDisplay = $filter('date')(scope.currentMonth, 'yyyy年MM月')

      scope.$watch 'currentMonth', (n, o) ->
        $state.go 'main.history.month', month: n if n isnt o

      $state.go 'main.history.month', month: scope.currentMonth

  ]


.controller 'AgentSchoolHistoryCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', 'loggedUser', 'currentAgent',
   'agentSchoolService', 'agentStatsService', 'agentSchoolDataService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, User, CurrentAgent, AgentSchool, Stats, SchoolData) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent
      scope.currentMonth = $stateParams.month
      scope.refresh = ->
        scope.d3Data = []
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        queue = [Stats.query(agent_id: currentAgent.id).$promise,
                 scope.waitForSchoolsReady()]
        $q.all(queue).then (q) ->
          console.log currentAgent.schools
          groups = _.groupBy(q[0], 'month')
          scope.lastActiveData = groups[scope.currentMonth]


          _.each currentAgent.schools, (s) ->
            s.stats = _.find scope.lastActiveData, (f) -> f.school_id == s.school_id
            s.stats.rate = scope.calcRate s.stats
            s.stats.childRate =  scope.calcChildRate s.stats

      scope.refresh()
  ]