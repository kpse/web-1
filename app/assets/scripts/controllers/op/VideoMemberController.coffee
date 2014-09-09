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
  ['$scope', '$rootScope', '$stateParams', 'schoolService', 'videoMemberService', 'senderService', 'relationshipService',
    (scope, rootScope, stateParams, School, VideoMember, Parent, Relationship) ->
      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.school_id
      scope.parents = VideoMember.query school_id: stateParams.school_id, (all)->
        _.forEach all, (p) ->
          Parent.get school_id: stateParams.school_id, id: p.id, type: 'p', (data)->
            p.detail = data
            p.reltaionship = Relationship.query school_id: stateParams.school_id, parent: p.detail.phone
        scope.loading = false

  ]





