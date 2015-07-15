angular.module('kulebaoAgent').controller 'AgentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter', '$modal', 'loggedUser', 'currentAgent',
   'agentSchoolService', 'agentPasswordService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, Modal, User, CurrentAgent, AgentSchool, Password) ->
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
        currentAgent.schools = AgentSchool.query agent_id: currentAgent.id, ->
          currentAgent.schools[0].activeData = [{date: '201412', count: 100}, {date: '201502', count: 88.65}, {date: '201501', count: 56.88}, {date: '201503', count: 12.02} ]
          currentAgent.schools[1].activeData = []
          _.each currentAgent.schools, (kg) ->
            kg.checked = false
            kg.lastActiveData = _.last(kg.activeData)

      scope.refresh()

      scope.goActiveUser = ->

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