angular.module('kulebaoAgent').controller 'AgentCommercialCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$state', '$modal', 'currentAgent', 'loggedUser', 'agentContractorService',
   'agentAdInSchoolService', 'agentSchoolService', 'imageCompressService', 'agentActivityService',
    (scope, $rootScope, stateParams, $q, $state, Modal, Agent, User, Contractor, AdInSchool, Schools, Compress, Activity) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      $state.go 'main.commercial.contractors' if $state.current.name == 'main.commercial'

      scope.currentStatus = (ad) ->
        status = _.find scope.allStatus, (s) -> ad.publishing? && ad.publishing.publish_status == s.publish_status
        status = 0 unless status?
        if status then status.display else ''

      scope.allStatus = [{publish_status: 0, display: '未提交'},
        {publish_status: 99, display: '等待审批'},
        {publish_status: 2, display: '审批通过'},
        {publish_status: 3, display: '拒绝发布'}]

      scope.userStatus =
        [{publish_status: 99, display: '提交审批'}]

      scope.adminStatus =
        [{publish_status: 2, display: '审批通过'},
          {publish_status: 3, display: '拒绝发布'}]

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
          scope.currentModal.hide()
          $state.go targetState if targetState?
          $state.reload()


      scope.adTypes = [
        {name: '商户', route: 'contractors'},
        {name: '活动', route: 'activities'}
      ]
  ]