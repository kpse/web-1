angular.module('kulebaoOp').controller 'OpVideoMemberCtrl',
  ['$scope', '$rootScope', '$location', 'schoolService', 'chargeService', 'videoMemberService',
    (scope, rootScope, $location, School, Charge, VideoMember) ->
      rootScope.tabName = 'video'
      scope.loading = true
      scope.kindergartens = School.query (all)->
        _.forEach all, (k) ->
          k.charge = Charge.query school_id: k.school_id
          k.videoMember = VideoMember.query school_id: k.school_id
        scope.loading = false

      scope.detail = (kg) ->
        $location.path "main/video_in_school/#{kg.school_id}"
  ]

angular.module('kulebaoOp').controller 'OpVideoMemberInSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService',
    (scope, rootScope, stateParams, $location, School, Class) ->
      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, (classes)->
          scope.navigateTo(classes[0]) if $location.path().indexOf '/class' <= 0
        scope.loading = false

      scope.navigateTo = (c) ->
        $location.path "main/video_in_school/#{stateParams.school_id}/class/#{c.class_id}"
  ]

angular.module('kulebaoOp').controller 'OpVideoMemberInClassCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'videoMemberService', 'senderService',
   'relationshipService', 'classService',
    (scope, rootScope, stateParams, $location, School, VideoMember, Parent, Relationship, Class) ->
      scope.loading = true
      scope.current_class = stateParams.class_id
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, ->
          scope.parents = VideoMember.query school_id: stateParams.school_id, (all)->
            _.forEach all, (p) ->
              Parent.get school_id: stateParams.school_id, id: p.id, type: 'p', (data)->
                p.detail = data
                p.reltaionship = Relationship.query school_id: stateParams.school_id, parent: p.detail.phone if p.detail.phone?
            scope.loading = false
          scope.currentClass = _.find scope.kindergarten.classes, (c) -> c.class_id == parseInt scope.current_class


      scope.display = (p) ->
        p.reltaionship? && p.reltaionship.$resolved && p.reltaionship[0].child.class_id == parseInt stateParams.class_id

      scope.accountsInSchool = (parents, classId) ->
        _.map (_.filter parents, (f) -> !classId? || scope.display(f)), (p) -> 'account': p.account, 'password': p.password

      scope.downloadEnabled = (parents)->
        _.any parents, (f) -> scope.display(f)
  ]





