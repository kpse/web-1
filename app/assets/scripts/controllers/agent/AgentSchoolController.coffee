angular.module('kulebaoAgent').controller 'AgentSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', '$q', 'currentAgent', 'loggedUser',
   'agentSchoolDataService',
    (scope, $rootScope, stateParams, location, $q, Agent, User, SchoolData) ->
      scope.loggedUser = User
      scope.currentAgent = Agent
      scope.$emit 'currentAgent', scope.currentAgent

      scope.$on 'schools_ready', (data)->
        _.each scope.currentAgent.schools, (s) ->
          s.stats = SchoolData.get agent_id: scope.currentAgent.id, kg: s.school_id

  ]