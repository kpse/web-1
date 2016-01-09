angular.module('kulebaoAgent').controller 'AgentActivitiesCtrl',
  ['$scope', '$rootScope', '$stateParams', '$q', '$state', '$modal', '$alert', '$filter', 'currentAgent', 'loggedUser', 'agentRawActivityService',
   'agentActivityInSchoolService', 'agentSchoolDataService', 'fullResponseService', 'agentContractorService',
    'agentActivityEnrollmentService',
    (scope, $rootScope, stateParams, $q, $state, Modal, Alert, $filter, Agent, User, Activity, ActivityInSchool,
     SchoolData, FullRes, Contractor, Enrollment) ->
      scope.adminUser = User
      scope.currentAgent = Agent

      nameOf = (ad) ->
        _.assign ad, contractor: (_.find scope.contractors, (c) -> c.id == ad.contractor_id)
        if ad.contractor?
          "#{scope.currentAgent.name}_商户_#{ad.contractor.title}_活动_#{ad.title}_报名.csv"
        else
          "#{scope.currentAgent.name}_活动_#{ad.title}_报名.csv"

      scope.refresh = (activityId) ->
        scope.cleanUpSearchText()
        $rootScope.loading = true
        queue = [FullRes(Activity, agent_id: scope.currentAgent.id)
                 scope.waitForSchoolsReady()
                 FullRes(Contractor, agent_id: scope.currentAgent.id)]

        $q.all(queue).then (q) ->
          scope.activities = q[0]
          activityGroup = _.groupBy scope.activities, 'contractor_id'
          scope.contractors = q[2]
          _.each scope.activities, (a) ->
            _.assign a, csvName : nameOf a
            [a.startDate, a.endDate] =  a.time_span.split('~') if a.time_span?
            a.actions = scope.actionsBaseOnStatus(scope.adminUser, a.publishing.publish_status)
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

            scope.distributedIn = (activity) ->
              _.filter scope.schools, (s) -> _.any s.activityIds, (c) -> c.activity_id == activity.id
            scope.parentsInSchools = (ad) ->
              _.sum (_.filter scope.distributedIn(ad), (f) -> f.lastActiveData?), (s) -> s.lastActiveData.data.logged_ever

            $rootScope.loading = false
          scope.resetSelection() if scope.selection?

      scope.refresh()

      scope.$on 'refresh', ->
        scope.refresh()

      scope.$on 'closeDialog', ->
        scope.currentModal.hide() if scope.currentModal?

      scope.enrollmentOfActivity = (ad) ->
        $q (resolve, reject) ->
          Enrollment.query agent_id: ad.agent_id, id: ad.id, (data) ->
              schoolsGroup = _.groupBy scope.schools, (s) -> s.school_id
              result = _.sortBy (_.map data, (d) -> {id: d.id, name: d.name, contact: d.contact, school: schoolsGroup[d.school_id][0].name}), 'id'
              if result.length > 0
                resolve(result)
              else
                alert "很抱歉，活动 #{ad.title} 暂时没有收到任何报名。"
                reject()
            , (err) -> reject(err)

      scope.getHeader = ->
        ['编号', '姓名', '联系方式', '学校']

      scope.editAd = (ad) ->
        _.assign ad, agent_id: scope.currentAgent.id
        if ad.contractor_id?
          _.assign ad, contractor: (_.find scope.contractors, (c) -> c.id == ad.contractor_id)
        scope.newAd = angular.copy ad
        scope.disableUploading = scope.dynamicDisable(scope.newAd)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/add_activity.html'

      scope.allowEditing = (user, ad) ->
        scope.canBeApproved(ad) || scope.canBeRejected(ad) || scope.canBePreviewed(ad) ||
          scope.canBeTakenOnline(ad) || scope.canBeTakenOffline(ad)

      scope.save = (newAd) ->
        newAd.contractor_id = newAd.contractor.id if newAd.contractor? && newAd.contractor.id?
        newAd.time_span = newAd.startDate + '~' + newAd.endDate
        newAd.$save ->
          scope.currentModal.hide()
          scope.refresh()

      scope.distribute = (activity) ->
        scope.currentActivity = angular.copy activity
        scope.resetSelection()
        scope.selectedSchools = _.filter scope.schools, (s) -> _.any s.activityIds, (c) -> c.activity_id == activity.id
        scope.unSelectedSchools = _.reject scope.schools, (r) ->
          _.find scope.selectedSchools, (u) -> r.school_id == u.school_id
        scope.cleanUpSearchText()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/agent/distribute_to_school.html'

      scope.disconnect = (kg, activity) ->
        connectionId = _.find kg.activityIds, (c) -> c.school_id == kg.school_id && c.activity_id == activity.id
        kg.activityIds = _.reject kg.activityIds, (c) -> c.school_id == kg.school_id && c.activity_id == activity.id
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
          r.checked = false
          r
        _.each $filter('filter')(scope.unSelectedSchools, this.searchText), (r) ->
          r.checked = check

      scope.checkAllDistributed = (check) ->
        scope.selectedSchools = [] unless scope.selectedSchools?
        scope.selectedSchools = _.map scope.selectedSchools, (r) ->
          r.checked = false
          r
        _.each $filter('filter')(scope.selectedSchools, this.searchText), (r) ->
          r.checked = check

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
        scope.currentAgent.schools = _.map scope.currentAgent.schools, (r) ->
          r.checked = false
          r

      handleError = (res) ->
        Alert
          title: '发布活动到学校出错，请稍后重试'
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

      scope.moveToTop = (ad) ->
        currentTop = (_.max scope.activities, 'priority') || {'priority': 0}
        ad.priority = currentTop.priority + 1
        ad.$save ->
          scope.refresh()
  ]