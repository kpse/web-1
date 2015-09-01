angular.module('kulebaoAgent')
.controller 'AgentHistoryCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', 'loggedUser', 'currentAgent',
   'agentStatsOperatorService'
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, User, CurrentAgent, OperatorStats) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      scope.pastMonths = [1..12].map (d) ->
        a = new Date()
        a.setDate(1)
        a.setMonth(a.getMonth() - d)
        a.getFullYear() + ('0' + (a.getMonth() + 1)).slice(-2) + ""

      scope.currentMonth = _.first scope.pastMonths
      scope.monthDisplay = (month) -> $filter('date')(Date.parse("#{month.substring(0, 4)}-#{month.slice(4)}-01"),
        'yyyy年MM月')
      scope.currentMonthDisplay = scope.monthDisplay scope.currentMonth
      scope.$watch 'currentMonth', (n, o) ->
        $state.go 'main.history.month', month: n if n isnt o

      $state.go 'main.history.month', month: scope.currentMonth

      scope.$on 'stats_ready', ->
        scope.export = (currentMonth) ->
          _(scope.currentAgent.schools).map (s) ->
            id: s.school_id
            name: s.name
            data: (_.find s.activeData, (d) -> d.month == currentMonth)
          .sortBy('school_id')
          .map (s) ->
            id: s.id
            name: s.name
            childrenCount: s.data.child_count
            parentsCount: s.data.logged_ever
            parentsLastMonth: s.data.logged_once
            childRate: s.data.childRate
            rate: s.data.rate
          .value()
        scope.exportHeader = ->
          ['编号', '学校名称', '学生数', '总用户数', '当月用户数', '当月激活率', '当月活跃度']
        scope.csvName = "#{scope.currentAgent.id}_#{scope.currentAgent.name}_#{scope.currentMonth}.csv"

      scope.forceToReCalculate = ->
        scope.loading = true
        OperatorStats.save ->
          $state.reload()
          scope.loading = false
  ]


.controller 'AgentSchoolHistoryCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', 'loggedUser', 'currentAgent',
   'agentSchoolService', 'agentStatsService', 'agentSchoolDataService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, User, CurrentAgent, AgentSchool, Stats, SchoolData) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent
      scope.currentMonth = $stateParams.month
      scope.currentMonthDisplay = scope.monthDisplay scope.currentMonth
      scope.allowToDelete = (month) ->
        month == scope.pastMonths[0] && scope.loggedUser.privilege_group == 'operator'
      scope.refresh = ->
        scope.d3Data = []
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        queue = [Stats.query(agent_id: currentAgent.id).$promise,
                 scope.waitForSchoolsReady()]
        $q.all(queue).then (q) ->
          groups = _.groupBy(q[0], 'month')
          scope.lastActiveData = groups[scope.currentMonth]
          _.each currentAgent.schools, (s) ->
            s.stats = _.find scope.lastActiveData, (f) -> f.school_id == s.school_id
            if s.stats?
              s.stats.rate = scope.calcRate s.stats
              s.stats.childRate = scope.calcChildRate s.stats
          scope.$emit 'stats_ready', currentAgent.schools

      scope.refresh()

      scope.deleteStats = (stats) ->
        Stats.delete id: stats.id, agent_id: scope.currentAgent.id, ->
          scope.refresh()
  ]