angular.module('kulebaoAgent').controller 'AgentContractorsCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$modal', '$alert', 'currentAgent', 'loggedUser', 'agentContractorService',
   'agentAdInSchoolService', 'agentSchoolService',
    (scope, $rootScope, stateParams, $q, Modal, Alert, Agent, User, Contractor, ContractorInSchool, Schools) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      scope.refresh = (contractorId) ->
        queue = [Contractor.query(agent_id: scope.currentAgent.id).$promise
                 Schools.query(agent_id: scope.currentAgent.id).$promise]

        $q.all(queue).then (q) ->
          scope.contractors = q[0]
          scope.schools = q[1]
          queue2 = _.map scope.schools, (s) ->
            ContractorInSchool.query(agent_id: scope.currentAgent.id, school_id: s.school_id).$promise
          $q.all(queue2).then (q2) ->
            group = _.groupBy (_.flatten q2), 'school_id'
            _.each scope.schools, (s) ->
              s.contractorIds = group[s.school_id]
            if scope.selectedSchools? && contractorId?
              scope.selectedSchools = _.filter scope.schools, (s) -> _.any s.contractorIds, (c) -> c.contractor_id == contractorId

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

      scope.distribute = (contractor) ->
        scope.resetSelection()
        scope.selectedSchools = _.filter scope.schools, (s) -> _.any s.contractorIds, (c) -> c.contractor_id == contractor.id
        scope.unSelectedSchools = _.reject scope.schools, (r) ->
          _.find scope.selectedSchools, (u) -> r.school_id == u.school_id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/distribute_to_school.html'

      scope.disconnect = (kg, contractor) ->
        connectionId = _.find kg.contractorIds, (c) -> c.school_id == kg.school_id
        kg.contractorIds = _.reject kg.contractorIds, (c) -> c.school_id == kg.school_id
        scope.selectedSchools = _.reject scope.selectedSchools, (s) -> s.school_id == kg.school_id
        scope.unSelectedSchools.push _.find scope.schools , (c) -> c.school_id == kg.school_id
        ContractorInSchool.delete(agent_id: contractor.id, school_id: kg.school_id, id: connectionId.id).$promise

      scope.connect = (kg, contractor) ->
        kg.contractorIds = [] unless kg.contractorIds?
        kg.contractorIds.push agent_id: contractor.id, school_id: kg.school_id, contractor_id: contractor.id
        scope.selectedSchools.push agent_id: contractor.id, school_id: kg.school_id, contractor_id: contractor.id, name: kg.name
        scope.unSelectedSchools = _.reject scope.unSelectedSchools , (k) -> k.school_id == kg.school_id
        ContractorInSchool.save(agent_id: contractor.id, school_id: kg.school_id, contractor_id: contractor.id).$promise

      scope.checkAll = (check) ->
        scope.unSelectedSchools = [] unless scope.unSelectedSchools?
        scope.unSelectedSchools = _.each scope.unSelectedSchools, (r) ->
          r.checked = check
          r

      scope.checkAllDistributed = (check) ->
        scope.selectedSchools = [] unless scope.selectedSchools?
        scope.selectedSchools = _.map scope.selectedSchools, (r) ->
          r.checked = check
          r

      scope.multipleDelete = ->
        checked = _.filter scope.selectedSchools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.disconnect kg, scope.currentAgent
        all = $q.all queue
        all.then (q) ->
            scope.resetSelection()
            scope.refresh(q[0].contractor_id)
          , (res) ->
            handleError res

      scope.multipleAdd = ->
        checked = _.filter scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.connect kg, scope.currentAgent
        all = $q.all queue
        all.then (q) ->
            scope.resetSelection()
            scope.refresh(q[0].contractor_id)
          , (res) ->
            handleError res

      scope.hasSelection = (kindergartens) ->
        _.some kindergartens, (r) -> r.checked? && r.checked == true

      scope.singleSelection = (kg) ->
        allChecked = _.every scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        scope.selection.allCheck = allChecked && scope.unSelectedSchools.length > 0

      scope.singleDistributedSelection = (kg) ->
        allChecked = _.every scope.selectedSchools, (r) -> r.checked? && r.checked == true
        scope.selection.allDistributedCheck = allChecked && scope.selectedSchools.length > 0

      scope.selection =
        allCheck: false
        allDistributedCheck: false

      scope.resetSelection = ->
        scope.selection =
          allCheck: false
          allDistributedCheck: false
        scope.checkAllDistributed false
        scope.checkAll false

      handleError = (res) ->
        Alert
          title: '无法保存发布信息'
          content: if res.data.error_msg? then res.data.error_msg else res.data
          placement: "top"
          type: "danger"
          show: true
          container: '.modal-dialog .panel-body'
          duration: 3
  ]