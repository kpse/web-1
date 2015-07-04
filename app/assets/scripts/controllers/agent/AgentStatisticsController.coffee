angular.module('kulebaoAgent').controller 'AgentStatisticsCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', '$location', '$filter','loggedUser', 'currentAgent',
   'agentSchoolService', 'schoolService',
    (scope, $rootScope, $stateParams, $state, $location, $filter, User, CurrentAgent, AgentSchool, AllSchool) ->
      scope.loggedUser = User
      scope.currentAgent = CurrentAgent
      console.log(scope.loggedUser)
      console.log($stateParams)

      if $stateParams.agent_id == 'default' || "#{scope.loggedUser.id}".indexOf('_') < 0
        $location.path "main/#{scope.loggedUser.id}/school"

      scope.$on 'currentAgent', (e, agent) ->
        scope.currentAgent = agent
        console.log scope.currentAgent

      scope.refresh = ->
        currentAgent = scope.currentAgent
        currentAgent.expireDisplayValue = $filter('date')(currentAgent.expire, 'yyyy-MM-dd')
        currentAgent.schoolIds = AgentSchool.query agentId: currentAgent.id, ->
          currentAgent.schools = _.map currentAgent.schoolIds, (kg) -> AllSchool.get(school_id: kg.school_id)
          _.each currentAgent.schools, (kg) -> kg.checked = false

      scope.refresh()

      scope.goActiveUser = ->

  ]