angular.module('kulebaoAgent').controller 'AgentCommercialCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q','currentAgent', 'loggedUser', 'agentAdService',
   'agentAdInSchoolService', 'agentSchoolService',
    (scope, $rootScope, stateParams, $q, Agent, User, AgentAd, AdInSchool, Schools) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      scope.refresh = ->
        queue = [AgentAd.query(agentId: scope.currentAgent.id).$promise
        Schools.query(agentId: scope.currentAgent.id).$promise]

        $q.all(queue).then (q) ->
          scope.commercials = q[0]
          scope.schools = q[1]
          _.each scope.schools, (s) ->
            s.ad = AdInSchool.query agentId: scope.currentAgent.id, kg: s.school_id, (data) ->
              _.each data, (d) ->
                commercial = _.find scope.commercials, (c) -> c.id == d.ad_id
                commercial.ads = [] unless commercial.ads?
                commercial.ads.push d

      scope.refresh()

      scope.editAd = (ad) ->

      scope.allStatus = ['审批通过', '等待审批', '拒绝发布']
  ]