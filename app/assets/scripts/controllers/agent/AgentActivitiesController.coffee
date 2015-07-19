angular.module('kulebaoAgent').controller 'AgentActivitiesCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$state', '$modal', '$alert', 'currentAgent', 'loggedUser', 'agentRawActivityService',
   'agentActivityInSchoolService', 'agentSchoolService', 'agentSchoolDataService',
    (scope, $rootScope, stateParams, $q, $state, Modal, Alert, Agent, User, Activity, ActivityInSchool, Schools, SchoolData) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      scope.refresh = (activityId) ->
        scope.loading = true
        queue = [Activity.query(agent_id: scope.currentAgent.id).$promise
                 scope.waitForSchoolsReady()]

        $q.all(queue).then (q) ->
          scope.activities = q[0]
          scope.schools = scope.currentAgent.schools
          _.each scope.schools, (s) ->
            s.stats = SchoolData.get agent_id: scope.currentAgent.id, school_id: s.school_id
          queue2 = _.map scope.schools, (s) ->
            ActivityInSchool.query(agent_id: scope.currentAgent.id, school_id: s.school_id).$promise
          $q.all(queue2).then (q2) ->
            group = _.groupBy (_.flatten q2), 'school_id'
            _.each scope.schools, (s) ->
              s.activityIds = group[s.school_id]
            if scope.selectedSchools? && activityId?
              scope.selectedSchools = _.filter scope.schools, (s) -> _.any s.activityIds, (c) -> c.activity_id == activityId
          scope.loading = false
        scope.resetSelection() if scope.selection?

      scope.refresh()

      scope.editAd = (ad) ->
        scope.newAd = angular.copy ad
        _.assign scope.newAd, agent_id: scope.currentAgent.id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_activity.html'

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

      scope.save = (newAd) ->
        newAd.contractor_id = newAd.contractor.id if newAd.contractor? && newAd.contractor.id?
        newAd.$save ->
          scope.currentModal.hide()
          scope.refresh()

      scope.distributedIn = (activity) ->
        _.filter scope.schools, (s) -> _.any s.activityIds, (c) -> c.activity_id == activity.id

      scope.distribute = (activity) ->
        scope.currentActivity = angular.copy activity
        scope.resetSelection()
        scope.selectedSchools = _.filter scope.schools, (s) -> _.any s.activityIds, (c) -> c.activity_id == activity.id
        scope.unSelectedSchools = _.reject scope.schools, (r) ->
          _.find scope.selectedSchools, (u) -> r.school_id == u.school_id
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/distribute_to_school.html'

      scope.disconnect = (kg, activity) ->
        connectionId = _.find kg.activityIds, (c) -> c.school_id == kg.school_id
        kg.activityIds = _.reject kg.activityIds, (c) -> c.school_id == kg.school_id
        scope.selectedSchools = _.reject scope.selectedSchools, (s) -> s.school_id == kg.school_id
        scope.unSelectedSchools.push _.find scope.schools , (c) -> c.school_id == kg.school_id
        ActivityInSchool.delete(agent_id: scope.currentAgent.id, school_id: kg.school_id, id: connectionId.id).$promise

      scope.connect = (kg, activity) ->
        kg.activityIds = [] unless kg.activityIds?
        kg.activityIds.push agent_id: scope.currentAgent.id, school_id: kg.school_id, activity_id: activity.id
        scope.selectedSchools.push agent_id: scope.currentAgent.id, school_id: kg.school_id, activity_id: activity.id, name: kg.name
        scope.unSelectedSchools = _.reject scope.unSelectedSchools , (k) -> k.school_id == kg.school_id
        ActivityInSchool.save(agent_id: scope.currentAgent.id, school_id: kg.school_id, activity_id: activity.id).$promise

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
        queue = _.map checked, (kg) -> scope.disconnect kg, scope.currentActivity
        all = $q.all queue
        all.then (q) ->
          scope.resetSelection()
          scope.refresh(q[0].activity_id)
        , (res) ->
          handleError res

      scope.multipleAdd = ->
        checked = _.filter scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.connect kg, scope.currentActivity
        all = $q.all queue
        all.then (q) ->
          scope.resetSelection()
          scope.refresh(q[0].activity_id)
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
          title: '发布活动到学校出错，请稍后重试'
          content: if res.data.error_msg? then res.data.error_msg else res.data
          placement: "top"
          type: "danger"
          show: true
          container: '.modal-dialog .panel-body'
          duration: 3
  ]