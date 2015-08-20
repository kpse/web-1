angular.module('kulebaoAgent').controller 'AgentCommercialCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$state', '$modal', '$filter', 'currentAgent', 'loggedUser',
   'agentContractorService',
   'agentContractorInSchoolService', 'agentSchoolService', 'imageCompressService', 'agentRawActivityService',
    (scope, $rootScope, stateParams, $q, $state, Modal, $filter, Agent, User, Contractor, AdInSchool, Schools, Compress, Activity) ->
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

      scope.actionsBaseOnStatus = (user, status) ->
        switch status
          when 0 then [{publish_status: 99, display: '提交审批'}]
          when 99
            if user.privilege_group == 'operator'
              [{publish_status: 2, display: '审批通过'},
                {publish_status: 3, display: '拒绝审批'}]
            else
              []
          when 2 then [{publish_status: 4, display: '上线'}, {publish_status: 5, display: '下线'}]
          when 3
            if user.privilege_group == 'operator'
              [{publish_status: 2, display: '审批通过'}]
            else
              [{publish_status: 99, display: '提交审批'}]
          when 4 then [{publish_status: 5, display: '下线'}]
          when 5
            if user.privilege_group == 'operator'
              [{publish_status: 4, display: '重新上线'}, {publish_status: 99, display: '重新提交审批'}]
            else
              [{publish_status: 99, display: '重新提交审批'}]
          else
            []

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
        today = new Date
        new Contractor
          priority: 0
          agent_id: scope.currentAgent.id
          publishing:
            publish_status: 0
          targetState: 'main.commercial.contractors'
          startDate: $filter('date')(today, 'yyyy-MM-dd')
          endDate: $filter('date')(today.setMonth(today.getMonth() + 1), 'yyyy-MM-dd')

      createNewActivity = ->
        today = new Date
        new Activity
          priority: 0
          agent_id: scope.currentAgent.id
          publishing:
            publish_status: 0
          targetState: 'main.commercial.activities'
          startDate: $filter('date')(today, 'yyyy-MM-dd')
          endDate: $filter('date')(today.setMonth(today.getMonth() + 1), 'yyyy-MM-dd')

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

      scope.save = (newAd) ->
        newAd.contractor_id = newAd.contractor.id if newAd.contractor? && newAd.contractor.id?
        newAd.time_span = newAd.startDate + '~' + newAd.endDate
        targetState = newAd.targetState
        newAd.$save ->
          scope.loading = true
          if scope.currentModal?
            scope.currentModal.hide()
          else
            scope.$broadcast 'closeDialog'
          if targetState?
            if $state.is targetState
              scope.refresh()
            else
              $state.go targetState, reload: true

            scope.loading = false


      scope.published = (ad) ->
        ad.publishing? && _.any [99, 2, 4], (c) -> ad.publishing.publish_status == c

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
        scope.adminUser.privilege_group == 'operator' || _.any [0, 99, 3], (c) -> ad.publishing.publish_status == c

      scope.adTypes = [
        {name: '商户', route: 'contractors'},
        {name: '活动', route: 'activities'}
      ]

      scope.allowToDistribute = (ad) -> ad.publishing && _.any [2, 4], (s) -> ad.publishing.publish_status == s

      scope.parentsInSchools = (schools) ->
        _.sum schools, (s) -> s.lastActiveData.logged_ever

      scope.adminEdit = (ad, oldStatus) ->
        switch ad.publishing.publish_status
          when 2 then scope.approve(ad)
          when 3 then scope.rejectDialog(ad)
          when 4 then scope.takeOnline(ad)
          when 5 then scope.putOffline(ad)
          when 99 then scope.preview(ad)
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
          if scope.currentModal?
            scope.currentModal.hide()
          else
            scope.$broadcast 'closeDialog'

      scope.takeOnline = (ad) ->
        ad.$active ->
          scope.refresh()
          if scope.currentModal?
            scope.currentModal.hide()
          else
            scope.$broadcast 'closeDialog'

      scope.putOffline = (ad) ->
        ad.$deactive ->
          scope.refresh()
          if scope.currentModal?
            scope.currentModal.hide()
          else
            scope.$broadcast 'closeDialog'

      scope.rejectDialog = (ad) ->
        scope.badAd = angular.copy ad
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/reject_commercial.html'

      scope.preview = (ad) ->
        ad.publishing =
          publish_status: 99
        ad.$save ->
          ad.$preview ->
            scope.refresh()
            if scope.currentModal?
              scope.currentModal.hide()
            else
              scope.$broadcast 'closeDialog'

      scope.removeAd = (newAd) ->
        newAd.$delete ->
          scope.refresh()
          if scope.currentModal?
            scope.currentModal.hide()
          else
            scope.$broadcast 'closeDialog'

      scope.reject = (ad) ->
        ad.publishing.publish_status = 3
        ad.$reject ->
          scope.refresh()
          if scope.currentModal?
            scope.currentModal.hide()
          else
            scope.$broadcast 'closeDialog'

      scope.cancelRejection = (ad) ->
        delete scope.badAd
        scope.refresh()
        if scope.currentModal?
          scope.currentModal.hide()
        else
          scope.$broadcast 'closeDialog'

      scope.hasPoint = (mapOptions) -> mapOptions.result?

      scope.savePoint = (ad, model) ->
        scope.pickUpModal.hide()
        ad.location =
          address: model.result.address
          latitude: model.result.point.lat
          longitude: model.result.point.lng


      scope.createOpts = (location) ->
        latitude: location.latitude ||  39.915
        longitude: location.longitude || 116.404

      scope.pickingUpPoint = (ad, form) ->
        scope.newAd = angular.copy ad
        scope.form = form
        if ad.location?
          scope.mapOptions = scope.createOpts(ad.location)
        else
          scope.mapOptions =
            city: scope.currentAgent.city || scope.currentAgent.area
            address: scope.currentAgent.area
        scope.pickUpModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/point_picking.html'

      scope.disableUploading = false
      scope.buttonLabel = '添加'
      scope.onUploadSuccess = (url) ->
        scope.$apply ->
          scope.newAd.logos.push url: url if url isnt undefined
          scope.disableUploading = scope.dynamicDisable(scope.newAd)
          scope.buttonLabel = scope.dynamicLabel(scope.newAd)

      scope.dynamicLabel = (message)->
        if message.logos.length == 0 then '添加' else '继续添加'

      scope.dynamicDisable = (message) ->
        message.logos && message.logos.length > 2

      scope.deleteLogo = (logo) ->
        _.pullAt scope.newAd.logos, _.findIndex scope.newAd.logos, 'url', logo.url
        scope.disableUploading = scope.dynamicDisable(scope.newAd)
        scope.buttonLabel = scope.dynamicLabel(scope.newAd)

      scope.validActivity = (ad) ->
        ad.location? && ad.logos? && ad.logos.length > 0 && ad.price?
      scope.validContractor = (ad) ->
        ad.location? && ad.location.address? && ad.logos? && ad.logos.length > 0

  ]