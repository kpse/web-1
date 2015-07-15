angular.module('kulebaoAgent').controller 'AgentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$modal', '$q', 'loggedUser', 'currentAgent',
   'agentSchoolService', 'agentPasswordService', 'agentStatsService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, Modal, $q, User, CurrentAgent, AgentSchool, Password, Stats) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent

      if $stateParams.agent_id == 'default' || "#{scope.loggedUser.id}".indexOf('_') < 0
        $location.path "main/#{scope.loggedUser.id}/school"

      scope.$on 'currentAgent', (e, agent) ->
        scope.currentAgent = agent

      scope.refresh = ->
        scope.d3Data = []
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        queue = [Stats.query(agent_id: currentAgent.id).$promise,
                 AgentSchool.query(agent_id: currentAgent.id).$promise]
        $q.all(queue).then (q) ->
          currentAgent.schools = q[1]
          groups = _.groupBy(q[0], 'school_id')
          _.each currentAgent.schools, (kg) ->
            kg.checked = false
            kg.activeData = groups[kg.school_id]
            _.each kg.activeData, (d) ->
              d.rate = d.logged_once/d.logged_ever * 100
            kg.lastActiveData = _.last(kg.activeData)

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