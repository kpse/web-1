angular.module('kulebaoAdmin').controller 'VideoMemberManagementCtrl',
  ['$scope', '$rootScope', '$state', '$stateParams', '$q', '$filter', 'chargeService', 'videoMemberService',
   'senderService', 'relationshipService', 'classService', 'parentService'
    (scope, rootScope, $state, $stateParams, $q, $filter, Charge, VideoMember, Parent, Relationship, Class, ParentSearch) ->
      rootScope.tabName = 'video'
      scope.loading = true
      extendFilterFriendlyProperties = (p) ->
        p.phone = p.detail.phone
        p.formattedPhone = $filter('phone')(p.detail.phone)
        p
      scope.kindergarten =
        classes: Class.query(school_id: $stateParams.kindergarten)

      queue = [Charge.query(school_id: $stateParams.kindergarten).$promise
               VideoMember.query(school_id: $stateParams.kindergarten).$promise]
      all = $q.all(queue)
      all.then (q) ->
        scope.charge = q[0]
        scope.parents = _.map q[1], (p) ->
          Parent.get school_id: $stateParams.kindergarten, id: p.id, type: 'p', (data)->
            p.detail = data
            p.reltaionship = Relationship.query
              school_id: $stateParams.kindergarten, parent: p.detail.phone if p.detail.phone?
            extendFilterFriendlyProperties(p)
          p

        scope.loading = false

      scope.display = (p, classId) ->
        p.reltaionship? && p.reltaionship.$resolved && p.reltaionship.length > 0 && p.reltaionship[0].child.class_id == classId

      scope.accountsInSchool = (parents, classId) ->
        _.map (_.filter parents, (f) -> !classId? || scope.display(f, classId)), (p) ->
          'account': p.account,
          'password': p.password,
          'name': p.detail.name + p.detail.phone

      scope.onSuccess = (importingData)->
        console.log importingData
        queue = [ParentSearch.query(school_id: $stateParams.kindergarten).$promise
          Relationship.query(school_id: $stateParams.kindergarten).$promise]
        all = $q.all(queue)
        all.then (q) ->
          group = _.groupBy q[0], 'phone'
          relationshipGroup = _.groupBy q[1], 'parent.phone'
          [scope.importingData, scope.errorDataNoPhone] = _.partition importingData, (d) -> _.has group, d['家长A手机号']

          scope.errorDataNoPhone = _.map scope.errorDataNoPhone, (d) -> d.error = '学校无此号码。';d

          [scope.importingData, scope.errorDataWrongName] = _.partition scope.importingData, (d) ->
            group[d['家长A手机号']]? && group[d['家长A手机号']][0].name == d['家长A姓名']

          scope.errorDataWrongName = _.map scope.errorDataWrongName, (d) -> d.error = '名字不匹配。';d

          [scope.importingData, scope.errorDataNoConnection] = _.partition scope.importingData, (d) -> _.has relationshipGroup, d['家长A手机号']

          scope.errorDataNoConnection = _.map scope.errorDataNoConnection, (d) -> d.error = '没有关联小孩。';d

          [scope.importingData, scope.errorDataClassNotMatch] = _.partition scope.importingData, (d) ->
            relationshipGroup[d['家长A手机号']]? && _.any relationshipGroup[d['家长A手机号']], (r) -> r.child.class_name == d['所属班级']

          scope.errorDataClassNotMatch = _.map scope.errorDataClassNotMatch, (d) -> d.error = '班级名称不匹配。';d

          [scope.enabledData, scope.importingData] = _.partition scope.importingData, (d) ->
            _.any scope.parents, (p) -> p.phone == d['家长A手机号']

          scope.enabledData = _.map scope.enabledData, (d) -> d.error = '该号码已经开通看宝宝。';d

          scope.errorData = _.flatten [scope.errorDataNoPhone, scope.errorDataWrongName, scope.errorDataNoConnection, scope.errorDataClassNotMatch]


  ]

angular.module('kulebaoAdmin').controller 'VideoMemberImportCtrl',
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

angular.module('kulebaoAdmin').controller 'OpVideoMemberInClassCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', '$filter', 'schoolService', 'videoMemberService',
   'senderService',
   'relationshipService', 'classService',
    (scope, rootScope, stateParams, $location, $filter, School, VideoMember, Parent, Relationship, Class) ->
      scope.loading = true

      scope.current_class = stateParams.class_id
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, ->
          scope.parents = VideoMember.query school_id: stateParams.school_id, (all)->
            _.forEach all, (p) ->
              Parent.get school_id: stateParams.school_id, id: p.id, type: 'p', (data)->
                p.detail = data
                p.reltaionship = Relationship.query
                  school_id: stateParams.school_id, parent: p.detail.phone if p.detail.phone?
                extendFilterFriendlyProperties(p)
            scope.loading = false
          scope.currentClass = _.find scope.kindergarten.classes, (c) -> c.class_id == parseInt scope.current_class


      scope.display = (p, classId) ->
        p.reltaionship? && p.reltaionship.$resolved && p.reltaionship.length > 0 && p.reltaionship[0].child.class_id == classId

      scope.accountsInSchool = (parents, classId) ->
        _.map (_.filter parents, (f) -> !classId? || scope.display(f, classId)), (p) ->
          'account': p.account,
          'password': p.password,
          'name': p.detail.name + p.detail.phone

      scope.downloadEnabled = (parents)->
        _.any parents, (f) -> scope.display(f)
  ]





