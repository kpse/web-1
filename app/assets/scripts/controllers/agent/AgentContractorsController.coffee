angular.module('kulebaoAgent').controller 'AgentContractorsCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$modal', '$alert', 'currentAgent', 'loggedUser', 'agentContractorService',
   'agentContractorInSchoolService', 'agentSchoolService', 'agentRawActivityService', 'agentSchoolDataService', 'fullResponseService',
    (scope, $rootScope, stateParams, $q, Modal, Alert, Agent, User, Contractor, ContractorInSchool, Schools, Activity,
     SchoolData, FullRes) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      scope.refresh = (contractorId) ->
        scope.loading = true
        queue = [FullRes(Contractor, agent_id: scope.currentAgent.id)
                 scope.waitForSchoolsReady()
                 FullRes Activity, agent_id: scope.currentAgent.id]

        $q.all(queue).then (q) ->
          scope.schools = scope.currentAgent.schools
          activityGroup = _.groupBy q[2], 'contractor_id'
          scope.contractors = _.map q[0], (c) ->
            c.activities = activityGroup[c.id]
            [c.startDate, c.endDate] =  c.time_span.split('~') if c.time_span?
            c
          _.each scope.schools, (s) ->
            s.stats = SchoolData.get agent_id: scope.currentAgent.id, school_id: s.school_id
          queue2 = _.map scope.schools, (s) ->
            ContractorInSchool.query(agent_id: scope.currentAgent.id, school_id: s.school_id).$promise
          $q.all(queue2).then (q2) ->
            group = _.groupBy (_.flatten q2), 'school_id'
            _.each scope.schools, (s) ->
              s.contractorIds = group[s.school_id]
            if scope.selectedSchools? && contractorId?
              scope.selectedSchools = _.filter scope.schools, (s) -> _.any s.contractorIds, (c) -> c.contractor_id == contractorId
            scope.loading = false
        scope.resetSelection() if scope.selection?

      scope.refresh()

      scope.$on 'refresh', ->
        scope.refresh()

      scope.$on 'closeDialog', ->
        scope.currentModal.hide() if scope.currentModal?

      scope.editAd = (ad) ->
        scope.newAd = angular.copy ad
        _.assign scope.newAd, agent_id: scope.currentAgent.id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_contractor.html'

      scope.save = (newAd) ->
        newAd.time_span = newAd.startDate + '~' + newAd.endDate
        newAd.$save ->
          scope.currentModal.hide()
          scope.refresh()

      scope.allowEditing = (user, ad) ->
        scope.canBeApproved(ad) || scope.canBeRejected(ad) || scope.canBePreviewed(ad) ||
          scope.canBeTakenOnline(ad) || scope.canBeTakenOffline(ad)

      scope.distributedIn = (contractor) ->
        _.filter scope.schools, (s) -> _.any s.contractorIds, (c) -> c.contractor_id == contractor.id

      scope.distribute = (contractor) ->
        scope.currentContractor = angular.copy contractor
        scope.resetSelection()
        scope.selectedSchools = scope.distributedIn contractor
        scope.unSelectedSchools = _.reject scope.schools, (r) ->
          _.find scope.selectedSchools, (u) -> r.school_id == u.school_id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/distribute_to_school.html'

      scope.disconnect = (kg, contractor) ->
        connectionId = _.find kg.contractorIds, (c) -> c.school_id == kg.school_id && c.contractor_id == contractor.id
        kg.contractorIds = _.reject kg.contractorIds, (c) -> c.school_id == kg.school_id && c.contractor_id == contractor.id
        scope.selectedSchools = _.reject scope.selectedSchools, (s) -> s.school_id == kg.school_id
        scope.unSelectedSchools.push _.find scope.schools , (c) -> c.school_id == kg.school_id
        ContractorInSchool.delete(agent_id: scope.currentAgent.id, school_id: kg.school_id, id: connectionId.id).$promise

      scope.connect = (kg, contractor) ->
        kg.contractorIds = [] unless kg.contractorIds?
        kg.contractorIds.push agent_id: scope.currentAgent.id, school_id: kg.school_id, contractor_id: contractor.id
        scope.selectedSchools.push agent_id: scope.currentAgent.id, school_id: kg.school_id, contractor_id: contractor.id, name: kg.name
        scope.unSelectedSchools = _.reject scope.unSelectedSchools , (k) -> k.school_id == kg.school_id
        ContractorInSchool.save(agent_id: scope.currentAgent.id, school_id: kg.school_id, contractor_id: contractor.id).$promise

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
        queue = _.map checked, (kg) -> scope.disconnect kg, scope.currentContractor
        all = $q.all queue
        all.then (q) ->
            scope.resetSelection()
            scope.refresh(q[0].contractor_id)
          , (res) ->
            handleError res

      scope.multipleAdd = ->
        checked = _.filter scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.connect kg, scope.currentContractor
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
          title: '发布商户到学校出错，请稍后重试'
          content: if res.data.error_msg? then res.data.error_msg else res.data
          placement: "top"
          type: "danger"
          show: true
          container: '.modal-dialog .panel-body'
          duration: 3

      scope.savePoint = (ad, model) ->
        scope.pickUpModal.hide()
        ad.location =
          address: model.result.address
          latitude: model.result.point.lat
          longitude: model.result.point.lng

      scope.pickingUpPoint = (ad, form) ->
        scope.newAd = angular.copy ad
        scope.form = form
        if ad.location?
          scope.mapOptions = scope.createOpts(ad.location)
        else
          scope.mapOptions =
            latitude: 39.915
            longitude: 116.404
        scope.pickUpModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/point_picking.html'
  ]