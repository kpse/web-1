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
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        currentAgent.schools = AgentSchool.query agent_id: currentAgent.id, ->
          _.each currentAgent.schools, (kg) -> kg.checked = false

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

  ]