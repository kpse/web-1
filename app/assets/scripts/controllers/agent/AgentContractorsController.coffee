angular.module('kulebaoAgent').controller 'AgentContractorsCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$modal', 'currentAgent', 'loggedUser', 'agentContractorService',
   'agentAdInSchoolService', 'agentSchoolService',
    (scope, $rootScope, stateParams, $q, Modal, Agent, User, Contractor, AdInSchool, Schools) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      scope.refresh = ->
        queue = [Contractor.query(agent_id: scope.currentAgent.id).$promise
                 Schools.query(agent_id: scope.currentAgent.id).$promise]

        $q.all(queue).then (q) ->
          scope.contractors = q[0]
          scope.schools = q[1]
          _.each scope.schools, (s) ->
            s.ad = AdInSchool.query agent_id: scope.currentAgent.id, kg: s.school_id, (data) ->
              _.each data, (d) ->
                commercial = _.find scope.contractors, (c) -> c.id == d.ad_id
                if commercial?
                  commercial.ads = [] unless commercial.ads?
                  commercial.ads.push d

      scope.refresh()

      scope.published = (ad) ->
        ad.publishing.published_at > 0
      scope.canBePreviewed = (ad) ->
        ad.id && scope.adminUser.privilege_group == 'agent' && (ad.publishing.publish_status == 0 || ad.publishing.publish_status == 3)

      scope.canBeApproved = (ad) ->
        ad.id && scope.adminUser.privilege_group == 'operator' && (ad.publishing.publish_status == 99 || ad.publishing.publish_status == 3)

      scope.canBeRejected = (ad) ->
        ad.id && scope.adminUser.privilege_group == 'operator' && (ad.publishing.publish_status == 99 || ad.publishing.publish_status == 2)


      scope.editAd = (ad) ->
        scope.newAd = angular.copy ad
        _.assign scope.newAd, agent_id: scope.currentAgent.id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_contractor.html'

      scope.allStatus = [{publish_status: 0, display: '未提交'},
        {publish_status: 99, display: '等待审批'},
        {publish_status: 2, display: '审批通过'},
        {publish_status: 3, display: '拒绝发布'}]

      scope.userStatus =
        [{publish_status: 99, display: '提交审批'}]

      scope.adminStatus =
        [{publish_status: 2, display: '审批通过'},
          {publish_status: 3, display: '拒绝发布'}]

      scope.allowEditing = (user, ad) ->
        scope.canBeApproved(ad) || scope.canBeRejected(ad) || scope.canBePreviewed(ad)

      scope.preview = (ad) ->
        ad.publishing =
          publish_status: 99
        ad.$preview ->
          scope.refresh()

      scope.removeAd = (newAd) ->
        newAd.$delete ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.save = (newAd) ->
        newAd.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.approve = (ad) ->
        ad.publishing =
          publish_status: 2
        ad.$approve ->
          scope.refresh()

      scope.rejectDialog = (ad) ->
        scope.badAd = angular.copy ad
        scope.badAd.publishing =
          publish_status: 3
          reject_reason: ''
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/reject_commercial.html'

      scope.reject = (ad) ->
        ad.$reject ->
          scope.refresh()
          scope.currentModal.hide()

      scope.adminEdit = (ad, oldStatus) ->
        switch ad.publishing.publish_status
          when 2 then scope.approve(ad)
          when 3 then scope.rejectDialog(ad)
          else
            console.log 'no way here! publish_status = ' + ad.publishing.publish_status
            ad.publishing.publish_status = parseInt oldStatus

  ]