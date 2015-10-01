angular.module('kulebaoAgent').controller 'AgentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$modal', '$q', 'loggedUser', 'currentAgent',
   'agentSchoolService', 'agentPasswordService', 'agentStatsService', 'fullResponseService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, Modal, $q, User, CurrentAgent, AgentSchool, Password, Stats, FullRes) ->
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

      scope.calcChildRate = (d) ->
        result = if d.child_count == 0 then 0 else (d.logged_ever / d.child_count * 1.5 * 100 ).toFixed 2
        if result > 100 then 100 else result

      scope.calcRate = (d) -> if !d? || !d.logged_ever? || d.logged_ever == 0 then 0 else (d.logged_once/d.logged_ever * 100).toFixed 2

      scope.refresh = ->
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
              d.rate = scope.calcRate(d.data)
              d.childRate = scope.calcChildRate(d.data)
            kg.lastActiveData = _.last _.sortBy kg.activeData, 'month'

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
        scope.d3Data = school.activeData

  ]