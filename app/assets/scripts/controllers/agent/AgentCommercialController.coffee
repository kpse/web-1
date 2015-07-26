angular.module('kulebaoAgent').controller 'AgentCommercialCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$state', '$modal', 'currentAgent', 'loggedUser', 'agentContractorService',
   'agentContractorInSchoolService', 'agentSchoolService', 'imageCompressService', 'agentRawActivityService',
    (scope, $rootScope, stateParams, $q, $state, Modal, Agent, User, Contractor, AdInSchool, Schools, Compress, Activity) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      $state.go 'main.commercial.contractors' if $state.current.name == 'main.commercial'

      scope.currentStatus = (ad) ->
        status = _.find scope.allStatus, (s) -> ad.publishing? && ad.publishing.publish_status == s.publish_status
        status = 0 unless status?
        if status then status.display else ''

      scope.refresh = ->
        scope.$broadcast 'refresh'

      scope.allStatus = [{publish_status: 0, display: '未提交'},
        {publish_status: 99, display: '等待审批'},
        {publish_status: 2, display: '审批通过'},
        {publish_status: 3, display: '审批未通过'},
        {publish_status: 4, display: '上线'},
        {publish_status: 5, display: '下线'}]

      scope.userStatus =
        [{publish_status: 99, display: '提交审批'},
          {publish_status: 4, display: '上线'},
          {publish_status: 5, display: '下线'}]

      scope.adminStatus =
        [{publish_status: 2, display: '审批通过'},
          {publish_status: 3, display: '审批未通过'},
          {publish_status: 4, display: '上线'},
          {publish_status: 5, display: '下线'}]


      scope.categories = ['亲子摄影', '培训教育', '亲子游乐', '亲子购物', '其他']

      scope.addNewAd = () ->
        scope.newAd = createNewAd()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_contractor.html'

      scope.addNewActivity = () ->
        scope.newAd = createNewActivity()
        scope.contractors = Contractor.query agent_id: scope.currentAgent.id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_activity.html'

      createNewAd = ->
        new Contractor
          agent_id: scope.currentAgent.id
          publishing:
            publish_status: 0
          targetState: 'main.commercial.contractors'

      createNewActivity = ->
        new Activity
          agent_id: scope.currentAgent.id
          publishing:
            publish_status: 0
          targetState: 'main.commercial.activities'

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

      scope.save = (newAd) ->
        newAd.contractor_id = newAd.contractor.id if newAd.contractor? && newAd.contractor.id?
        targetState = newAd.targetState
        newAd.$save ->
          scope.loading = true
          scope.currentModal.hide()
          if targetState?
            $state.go targetState, reload: true unless $state.is targetState
            $state.reload() if $state.is targetState
            scope.loading = false


      scope.published = (ad) ->
        ad.publishing? && ad.publishing.published_at > 0

      scope.canBePreviewed = (ad) ->
        ad.id && (ad.publishing.publish_status == 0 || ad.publishing.publish_status == 3)

      scope.canBeTakenOnline = (ad) ->
        ad.id && ad.publishing.publish_status == 2

      scope.canBeTakenOffline = (ad) ->
        ad.id && ad.publishing.publish_status == 4

      scope.canBeApproved = (ad) ->
        ad.id && scope.adminUser.privilege_group == 'operator' && (ad.publishing.publish_status == 99 || ad.publishing.publish_status == 3)

      scope.canBeRejected = (ad) ->
        ad.id && scope.adminUser.privilege_group == 'operator' && (ad.publishing.publish_status == 99 || ad.publishing.publish_status == 2)

      scope.allowEditingContent = (ad) ->
        _.any [0, 1, 3, 5], (c) -> ad.publishing.publish_status == c

      scope.adTypes = [
        {name: '商户', route: 'contractors'},
        {name: '活动', route: 'activities'}
      ]

      scope.allowToDistribute = (ad) -> ad.publishing && _.any [2, 4], (s) -> ad.publishing.publish_status == s

      scope.parentsInSchools = (schools) ->
        _.sum schools, (s) -> s.stats.all

      scope.adminEdit = (ad, oldStatus) ->
        switch ad.publishing.publish_status
          when 2 then scope.approve(ad)
          when 3 then scope.rejectDialog(ad)
          when 4 then scope.takeOnline(ad)
          when 5 then scope.putOffline(ad)
          else
            console.log 'no way here! publish_status = ' + ad.publishing.publish_status
            ad.publishing.publish_status = parseInt oldStatus
      scope.userEdit = (ad, oldStatus) ->
        switch ad.publishing.publish_status
          when 99 then scope.preview(ad)
          when 4 then scope.takeOnline(ad)
          when 5 then scope.putOffline(ad)
          else
            console.log 'no way here! publish_status = ' + ad.publishing.publish_status
            ad.publishing.publish_status = parseInt oldStatus

      scope.approve = (ad) ->
        ad.$approve ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.takeOnline = (ad) ->
        ad.$active ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.putOffline = (ad) ->
        ad.$deactive ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.rejectDialog = (ad) ->
        scope.badAd = angular.copy ad
        scope.badAd.publishing =
          publish_status: 3
          reject_reason: ''
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/reject_commercial.html'

      scope.preview = (ad) ->
        ad.publishing =
          publish_status: 99
        ad.$preview ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.removeAd = (newAd) ->
        newAd.$delete ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.save = (newAd) ->
        newAd.$save ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.reject = (ad) ->
        ad.$reject ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?
  ]