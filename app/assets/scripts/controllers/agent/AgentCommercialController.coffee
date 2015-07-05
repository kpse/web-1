angular.module('kulebaoAgent').controller 'AgentCommercialCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$modal', 'currentAgent', 'loggedUser', 'agentAdService',
   'agentAdInSchoolService', 'agentSchoolService', 'imageCompressService',
    (scope, $rootScope, stateParams, $q, Modal, Agent, User, AgentAd, AdInSchool, Schools, Compress) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      scope.refresh = ->
        queue = [AgentAd.query(agent_id: scope.currentAgent.id).$promise
        Schools.query(agent_id: scope.currentAgent.id).$promise]

        $q.all(queue).then (q) ->
          scope.commercials = q[0]
          scope.schools = q[1]
          _.each scope.schools, (s) ->
            s.ad = AdInSchool.query agent_id: scope.currentAgent.id, kg: s.school_id, (data) ->
              _.each data, (d) ->
                commercial = _.find scope.commercials, (c) -> c.id == d.ad_id
                if commercial?
                  commercial.ads = [] unless commercial.ads?
                  commercial.ads.push d

      scope.refresh()
      scope.published = (ad) ->
        ad.publishing.published_at > 0

      scope.editAd = (ad) ->
        scope.newAd = angular.copy ad
        _.assign scope.newAd, agent_id: scope.currentAgent.id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_commercial.html'

      scope.allStatus = [
        {value: 0, display: '未提交'},
        {value: 99, display: '等待审批'},
        {value: 2, display: '审批通过'},
        {value: 3, display: '拒绝发布'}]

      scope.allTags = ['商户:亲子摄影', '商户:亲子游乐', '商户:幼儿教育', '商户:亲子购物', '商户:DIY手工', '活动:线上', '活动:线下']

      scope.addNewAd = () ->
        scope.newAd = createNewAd()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_commercial.html'

      createNewAd = ->
        new AgentAd
          agent_id: scope.currentAgent.id

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

      scope.preview = (newAd) ->
        newAd.$preview ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.removeAd = (newAd) ->
        newAd.$delete ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.save = (newAd) ->
        newAd.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.closeDialog = (newAd) ->
        scope.currentModal.hide()
  ]