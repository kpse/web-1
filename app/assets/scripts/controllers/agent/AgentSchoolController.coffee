angular.module('kulebaoAgent').controller 'AgentSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', '$q', 'currentAgent', 'loggedUser',
   'agentSchoolDataService',
    (scope, $rootScope, stateParams, location, $q, Agent, User, SchoolData) ->
      scope.loggedUser = User
      scope.currentAgent = Agent
      scope.$emit 'currentAgent', scope.currentAgent

      scope.$on 'schools_ready', ->
        scope.refresh()

      scope.refresh()

      scope.refresh = ->
        _.each scope.currentAgent.schools, (s) ->
          s.stats = SchoolData.get agent_id: scope.currentAgent.id, school_id: s.school_id
          s.csvName = "#{s.school_id}_#{s.name}活跃度.csv"

      scope.export = (kg) ->
        data = _.find scope.currentAgent.schools, (k) -> k.school_id == kg.school_id
        sorted = _.sortBy data.activeData, 'month'
        _.map sorted, (s) -> month: s.month, childrenCount: s.child_count, parentsCount: s.logged_ever, rate: s.rate
      scope.exportHeader = ->
        ['月份', '学生数', '家长数', '月活跃度']
  ]