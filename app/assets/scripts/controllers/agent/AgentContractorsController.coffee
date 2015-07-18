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
          scope.currentModal.hide() if scope.currentModal?

      scope.removeAd = (newAd) ->
        newAd.$delete ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.save = (newAd) ->
        newAd.$save ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.approve = (ad) ->
        ad.publishing =
          publish_status: 2
        ad.$approve ->
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

      scope.reject = (ad) ->
        ad.$reject ->
          scope.refresh()
          scope.currentModal.hide() if scope.currentModal?

      scope.adminEdit = (ad, oldStatus) ->
        switch ad.publishing.publish_status
          when 2 then scope.approve(ad)
          when 3 then scope.rejectDialog(ad)
          else
            console.log 'no way here! publish_status = ' + ad.publishing.publish_status
            ad.publishing.publish_status = parseInt oldStatus

      scope.distribute = (ad) ->
        scope.resetSelection()
        scope.selectedSchools = ad.schools
        scope.unSelectedSchools = _.reject scope.currentAgent.schools, (r) ->
          _.find ad.schools, (u) ->
            r.school_id == u.school_id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/distribute_to_school.html'

      scope.disconnect = (kg, contractor) ->
        deletedSchool = _.find contractor.schoolIds, (k) -> k.school_id == kg.school_id
        contractor.schoolIds = _.reject contractor.schoolIds, (k) -> k.school_id == kg.school_id
        contractor.schools = _.reject contractor.schools, (k) -> k.school_id == kg.school_id
        scope.unSelectedSchools.push _.find scope.kindergartens , (k) -> k.school_id == kg.school_id
#        AgentSchool.delete(agent_id: contractor.id, kg: deletedSchool.id).$promise
#        alert contractor.schools

      scope.connect = (kg, contractor) ->
        contractor.schoolIds = [] unless contractor.schoolIds?
        contractor.schools = [] unless contractor.schools?
        contractor.schoolIds.push school_id: kg.school_id
        contractor.schools.push _.find scope.kindergartens, (k) -> k.school_id == kg.school_id
        scope.unSelectedSchools = _.reject scope.unSelectedSchools , (k) -> k.school_id == kg.school_id
#        AgentSchool.save(agent_id: contractor.id, school_id: kg.school_id, name: kg.full_name).$promise
#        alert contractor.schools

      scope.checkAll = (check) ->
        scope.unSelectedSchools = [] unless scope.unSelectedSchools?
        scope.unSelectedSchools = _.each scope.unSelectedSchools, (r) ->
          r.checked = check
          r

      scope.checkAgentAll = (check) ->
        if scope.currentAgent?
          scope.selectedSchools = _.map scope.selectedSchools, (r) ->
            r.checked = check
            r

      scope.multipleDelete = ->
        checked = _.filter scope.selectedSchools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.disconnect kg, scope.currentAgent
        all = $q.all queue
        all.then (q) ->
          scope.resetSelection()
          scope.refresh(scope.currentAgent)
        , (res) ->
          handleError res

      scope.multipleAdd = ->
        checked = _.filter scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.connect kg, scope.currentAgent
        all = $q.all queue
        all.then (q) ->
          scope.resetSelection()
          scope.refresh(scope.currentAgent)
        , (res) ->
          handleError res

      scope.hasSelection = (kindergartens) ->
        _.some kindergartens, (r) -> r.checked? && r.checked == true

      scope.singleSelection = (kg) ->
        allChecked = _.every scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        scope.selection.allCheck = allChecked && scope.unSelectedSchools.length > 0

      scope.selection =
        allCheck: false
        allAgentCheck: false

      scope.resetSelection = ->
        scope.selection =
          allCheck: false
          allAgentCheck: false
        scope.checkAgentAll false
        scope.checkAll false
  ]