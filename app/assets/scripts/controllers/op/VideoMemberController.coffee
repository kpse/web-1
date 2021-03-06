angular.module('kulebaoOp').controller 'OpVideoMemberCtrl',
  ['$scope', '$rootScope', '$state', 'schoolService', 'chargeService', 'videoMemberService',
    (scope, rootScope, $state, School, Charge, VideoMember) ->
      rootScope.tabName = 'video'
      rootScope.loading = true
      scope.kindergartens = School.query video_member_enabled: true, (all)->
        _.each all, (k) ->
          k.charge = Charge.query school_id: k.school_id
          k.videoMember = VideoMember.query school_id: k.school_id
        rootScope.loading = false

      scope.detail = (kg) ->
        $state.go "main.video_school", school_id: kg.school_id
  ]

angular.module('kulebaoOp').controller 'OpVideoMemberInSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', 'schoolService', 'classService',
    (scope, rootScope, stateParams, $state, School, Class) ->
      rootScope.tabName = 'video'
      rootScope.loading = true
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, (classes)->
          scope.navigateTo(classes[0]) unless $state.is 'main.video_school.class'
        rootScope.loading = false

      scope.navigateTo = (c) ->
        $state.go 'main.video_school.class', {school_id: stateParams.school_id, class_id: c.class_id}
  ]

angular.module('kulebaoOp').controller 'OpVideoMemberInClassCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', '$filter', 'schoolService', 'videoMemberService', 'senderService',
   'relationshipService', 'classService',
    (scope, rootScope, stateParams, $location, $filter, School, VideoMember, Parent, Relationship, Class) ->
      rootScope.loading = true
      extendFilterFriendlyProperties = (p) ->
        p.phone = p.detail.phone
        p.formattedPhone = $filter('phone')(p.detail.phone)
        p

      scope.current_class = stateParams.class_id
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, ->
          scope.parents = VideoMember.query school_id: stateParams.school_id, (all)->
            _.each all, (p) ->
              Parent.get school_id: stateParams.school_id, id: p.id, type: 'p', (data)->
                p.detail = data
                p.relationship = Relationship.query school_id: stateParams.school_id, parent: p.detail.phone if p.detail.phone?
                extendFilterFriendlyProperties(p)
            rootScope.loading = false
          scope.currentClass = _.find scope.kindergarten.classes, (c) -> c.class_id == parseInt scope.current_class


      scope.display = (p, classId) ->
        connected = p.relationship? && p.relationship.$resolved && p.relationship.length > 0
        if classId?
          connected && p.relationship[0].child.class_id == classId
        else
          connected

      scope.accountsInSchool = (parents, classId) ->
        _.map (_.filter parents, (f) -> !classId? || scope.display(f, classId)), (p) -> 'account': p.account, 'password': p.password, 'name': p.detail.phone + p.detail.name

      scope.downloadEnabled = (parents)->
        _.any parents, (f) -> scope.display(f)
  ]





