angular.module('kulebaoAgent').controller 'AgentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$modal', '$q', 'loggedUser', 'currentAgent',
   'agentSchoolService', 'agentPasswordService', 'agentStatsService', 'fullResponseService', 'monthlyActiveRateService', 'totalActiveRateService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, Modal, $q, User, CurrentAgent, AgentSchool, Password, Stats, FullRes, MonthlyActive, TotalActive) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      if $stateParams.agent_id == 'default' || "#{scope.loggedUser.id}".indexOf('_') < 0
        $location.path "main/#{scope.loggedUser.id}/school"

      scope.$on 'currentAgent', (e, agent) ->
        scope.currentAgent = agent

      scope.waitForSchoolsReady = ->
        $q (resolve, reject) ->
          scope.$on 'schools_ready', -> resolve()
          resolve() if scope.currentAgent && scope.currentAgent.schools?

      scope.waitForSchoolsWeeklyReportReady = ->
        $q (resolve, reject) ->
          scope.$on 'weekly_stats_ready', -> resolve()
          resolve() if scope.currentAgent && scope.currentAgent.schools?

      scope.calcMonthlyActiveRate = MonthlyActive

      scope.calcTotalActiveRate = TotalActive

      scope.refresh = ->
        $rootScope.loading = true
        scope.d3Data = []
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        queue = [Stats.query(agent_id: currentAgent.id).$promise,
                 FullRes AgentSchool, agent_id: currentAgent.id ]
        $q.all(queue).then (q) ->
          currentAgent.schools = q[1]
          groups = _.groupBy(q[0], (d) -> d.data.school_id)
          _.each currentAgent.schools, (kg) ->
            kg.checked = false
            kg.activeData = _.uniq groups[kg.school_id], (u) -> u.data.month
            _.each kg.activeData, (d) ->
              d.rate = scope.calcTotalActiveRate(d.data)
              d.childRate = scope.calcMonthlyActiveRate(d.data)
            kg.lastActiveData = _.last _.sortBy kg.activeData, 'month'
          $rootScope.loading = false

          scope.$broadcast 'schools_ready', currentAgent.schools

      scope.refresh()

      scope.editAgent = (agent) ->
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/view_agent.html'

      scope.changePassword = (agent) ->
        scope.user = angular.copy agent
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/change_password.html'

      scope.change = (agent) ->
        Password.save (_.assign agent, agent_id: agent.id), ->
          scope.currentModal.hide()

      scope.checkUserActive = (school) ->
        scope.currentSchool = school
        scope.d3Data = _.map school.activeData, (a) -> a.month = a.data.month;a

      scope.$on '$stateChangeStart', ->
        $rootScope.loading = true
  ]