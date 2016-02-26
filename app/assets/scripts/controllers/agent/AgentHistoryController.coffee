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
            created_at: $filter('date')(s.created_at, 'yyyy-MM-dd')
            address: s.address
            data: (_.find s.activeData, (d) -> d.data.month == currentMonth)
          .filter (s) ->
            s.data?
          .sortBy('school_id')
          .map (s) ->
            s.monthlyData = s.data.data
            id: s.id
            created_at: s.created_at
            name: s.name
            address: s.address
            childrenCount: s.monthlyData.child_count
            parentsCount: s.monthlyData.parent_count
            parentsEverLogged: s.monthlyData.logged_ever
            parentsLastMonth: s.monthlyData.logged_once
          .value()
        scope.exportHeader = ->
          ['学校ID', '开园时间', '学校全称', '地址', '学生数', '家长总数', '总用户数', '当月用户数']
        scope.csvName = "#{scope.currentAgent.id}_#{scope.currentAgent.name}_#{scope.currentMonth}.csv"

        _.each scope.currentAgent.schools, (s) ->
          s.csvName = "#{s.school_id}_#{s.name}历史数据汇总.csv"
        scope.singleExport = (kg) ->
          data = _.find scope.currentAgent.schools, (k) -> k.school_id == kg.school_id
          sorted = _.sortBy data.activeData, 'month'
          _.map sorted, (s) ->
            id: data.school_id
            name: data.name
            month: s.data.month
            childrenCount: s.data.child_count
            parentsCount: s.data.parent_count
            parentsEverCount: s.data.logged_ever
            parentsOnceLogged: s.data.logged_once
        scope.singleExportHeader = ->
          ['学校ID', '学校全称', '月份', '学生数', '家长总数', '总用户数', '当月用户数']


      scope.forceToReCalculate = ->
        $rootScope.loading = true
        OperatorStats.save ->
          $state.reload()
          $rootScope.loading = false

      scope.goWeekly = ->
        $state.go 'main.weekly_history'
  ]

.controller 'AgentSchoolHistoryCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', 'loggedUser', 'currentAgent',
   'agentStatsService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, User, CurrentAgent, Stats) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent
      scope.currentMonth = $stateParams.month
      scope.currentMonthDisplay = scope.monthDisplay scope.currentMonth
      scope.allowToDelete = (month, stats) ->
        stats && month == scope.pastMonths[0] && scope.loggedUser.privilege_group == 'operator'
      scope.refresh = ->
        $rootScope.loading = true
        scope.d3Data = []
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        queue = [Stats.query(agent_id: currentAgent.id).$promise,
                 scope.waitForSchoolsReady()]
        $q.all(queue).then (q) ->
          groups = _.groupBy(q[0], (d) -> d.data.month )
          scope.lastActiveData = groups[scope.currentMonth]
          _.each currentAgent.schools, (s) ->
            s.stats = _.find scope.lastActiveData, (f) -> f.data.school_id == s.school_id
            if s.stats?
              s.stats.totalActive = scope.calcTotalActiveRate s.stats.data
              s.stats.monthlyActive = scope.calcMonthlyActiveRate s.stats.data
              s.stats = _.assign s.stats, s.stats.data
          scope.$emit 'stats_ready', currentAgent.schools
          $rootScope.loading = false

      scope.refresh()

      scope.deleteStats = (stats) ->
        Stats.delete id: stats.id, agent_id: scope.currentAgent.id, ->
          scope.refresh()
  ]

.controller 'AgentWeeklyHistoryCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', 'loggedUser', 'currentAgent',
    'agentWeekStatsService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, User, CurrentAgent, Stats) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      scope.refresh = ->
        $rootScope.loading = true
        currentAgent = scope.currentAgent
        scope.allWeeklyData = []

        queue = [Stats.query(agent_id: currentAgent.id).$promise,
          scope.waitForSchoolsReady()]
        $q.all(queue).then (q) ->
          scope.allWeeklyData = q[0]
          groups = _.groupBy(scope.allWeeklyData, (d) -> d.school_id )
          _.each currentAgent.schools, (s) ->
            s.weeklyStats = _.map groups[s.school_id], (w) ->
              w.totalActive = scope.calcTotalActiveRate w
              w.weeklyActive = scope.calcMonthlyActiveRate w
              w
            s.weeklyGroup = _.groupBy s.weeklyStats, 'week_start'
          scope.$emit 'weekly_stats_ready', currentAgent.schools


          scope.pastWeeks = (_.sortBy _.keys(_.groupBy(q[0], 'week_start')), 'week_start').reverse()
          scope.pastWeekends = (_.sortBy _.keys(_.groupBy(q[0], 'week_end')), 'week_end').reverse()

          scope.display = (week) ->
            weekMap = (_.zipObject scope.pastWeeks, scope.pastWeekends)
            week + '~' + weekMap[week]
          scope.currentWeek = _.first scope.pastWeeks
          scope.currentWeekDisplay = scope.display(scope.currentWeek)
          scope.export = ->
            _(scope.currentAgent.schools).map (s) ->
              id: s.school_id
              name: s.name
              created_at: $filter('date')(s.created_at, 'yyyy-MM-dd')
              address: s.address
              data: scope.currentWeekDataOf(s.weeklyGroup)
            .filter (s) ->
              s.data?
            .sortBy('school_id')
            .map (s) ->
              id: s.id
              created_at: s.created_at
              name: s.name
              address: s.address
              childrenCount: s.data.child_count
              parentsCount: s.data.parent_count
              parentsEverLogged: s.data.logged_ever
              parentsLastMonth: s.data.logged_once
            .value()
          scope.exportHeader = ->
            ['学校ID', '开园时间', '学校全称', '地址', '学生数', '家长总数', '总用户数', '当周用户数']
          scope.csvName = "#{scope.currentAgent.id}_#{scope.currentAgent.name}_#{scope.display(scope.currentWeek)}.csv"
          $rootScope.loading = false

      scope.refresh()
      scope.currentWeekDataOf = (week) ->
        if week?
          _.first week[scope.currentWeek]
        else
          NaN

      scope.$watch 'currentWeek', (n, o) ->
        scope.currentWeekDisplay = scope.display(scope.currentWeek) if scope.currentWeek?
        $state.go 'main.weekly_history.week', week: n if n isnt o

      $state.go 'main.weekly_history.week', month: scope.currentWeek

      scope.goMonthly = ->
        $state.go 'main.history'
  ]

.controller 'AgentSchoolWeeklyHistoryCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$q', '$timeout', 'loggedUser', 'currentAgent',
    (scope, $rootScope, $stateParams, $state, $location, $filter, $q, $timeout, User, CurrentAgent) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent
      scope.currentWeek = $stateParams.week
      scope.waitForSchoolsWeeklyReportReady().then ->
        $rootScope.loading = false
  ]