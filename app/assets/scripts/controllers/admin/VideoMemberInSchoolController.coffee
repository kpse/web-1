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
        [defaultAccounts, parents] = _.partition q[1], (a) -> a.id == 'default'
        parents = _.map parents, (p) ->
          Parent.get school_id: $stateParams.kindergarten, id: p.id, type: 'p', (data)->
            p.detail = data
            p.reltaionship = Relationship.query
              school_id: $stateParams.kindergarten, parent: p.detail.phone if p.detail.phone?
            extendFilterFriendlyProperties(p)
          p
        defaultAccounts = _.map defaultAccounts, (d) ->
          d.detail =
             name : '试用账号'
             phone :'试用账号'
          d
        $state.go 'kindergarten.video_default', kindergarten: $stateParams.kindergarten if defaultAccounts.length > 0
        scope.parents = _.flatten [parents, defaultAccounts]
        scope.loading = false

      scope.display = (p, classId) ->
        p.reltaionship? && p.reltaionship.$resolved && p.reltaionship.length > 0 && p.reltaionship[0].child.class_id == classId

      scope.accountsInSchool = (parents, classId) ->
        _.map (_.filter parents, (f) -> !classId? || scope.display(f, classId)), (p) ->
          'account': p.account,
          'password': p.password,
          'name': p.detail.name + p.detail.phone

      phoneFieldName = '家长A手机号'
      nameFieldName = '家长A姓名'
      classFieldName = '所属班级'

      validateData = (data) ->
        data? && _.all data, (d) -> _.all [phoneFieldName, nameFieldName, classFieldName], (n) -> _.has d, n

      scope.onSuccess = (importingData)->
        return alert("导入文件格式错误，每行至少要包含‘#{phoneFieldName}’，‘#{nameFieldName}’， ‘#{classFieldName}’列，请检查excel内容。") unless validateData(importingData)
        console.log importingData
        queue = [ParentSearch.query(school_id: $stateParams.kindergarten).$promise
          Relationship.query(school_id: $stateParams.kindergarten).$promise]
        all = $q.all(queue)
        all.then (q) ->
          group = _.groupBy q[0], 'phone'
          relationshipGroup = _.groupBy q[1], 'parent.phone'

          [scope.importingData, scope.errorDataNoPhone] = _.partition importingData, (d) -> _.has group, d[phoneFieldName]

          scope.errorDataNoPhone = _.map scope.errorDataNoPhone, (d) -> d.error = '学校无此号码。';d

          [scope.importingData, scope.errorDataWrongName] = _.partition scope.importingData, (d) ->
            group[d[phoneFieldName]]? && group[d[phoneFieldName]][0].name == d[nameFieldName]

          scope.importingData = _.map scope.importingData, (d) -> d.detail = group[d[phoneFieldName]][0];d

          scope.errorDataWrongName = _.map scope.errorDataWrongName, (d) -> d.error = '名字不匹配。';d

          [scope.importingData, scope.errorDataNoConnection] = _.partition scope.importingData, (d) -> _.has relationshipGroup, d[phoneFieldName]

          scope.importingData = _.map scope.importingData, (d) -> d.relationship = relationshipGroup[d[phoneFieldName]];d

          scope.errorDataNoConnection = _.map scope.errorDataNoConnection, (d) -> d.error = '没有关联小孩。';d

          [scope.importingData, scope.errorDataClassNotMatch] = _.partition scope.importingData, (d) ->
            relationshipGroup[d[phoneFieldName]]? && _.any relationshipGroup[d[phoneFieldName]], (r) -> r.child.class_name == d[classFieldName]

          scope.errorDataClassNotMatch = _.map scope.errorDataClassNotMatch, (d) -> d.error = '班级名称不匹配。';d

          [scope.enabledData, scope.importingData] = _.partition scope.importingData, (d) ->
            _.any scope.parents, (p) -> p.phone == d[phoneFieldName]

          scope.enabledData = _.map scope.enabledData, (d) -> d.error = '该号码已经开通看宝宝。';d

          scope.errorData = _.flatten [scope.errorDataNoPhone, scope.errorDataWrongName, scope.errorDataNoConnection, scope.errorDataClassNotMatch]

      scope.import = (data) ->
        scope.loading = true
        queue = _.map data, (d) -> VideoMember.save(school_id: d.detail.school_id, id: d.detail.parent_id).$promise
        all = $q.all queue
        all.then ->
          $state.reload()
      scope.cancelImporting = ->
        scope.loading = true
        delete scope.importingData
        delete scope.errorData
        delete scope.enabledData
        scope.loading = false

      scope.deleteAccount = (parent) ->
        scope.loading = true
        VideoMember.delete school_id: parent.school_id, id: parent.parent_id, ->
          $state.reload()
          scope.loading = false
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

angular.module('kulebaoAdmin').controller 'VideoMemberDefaultAccountWarningCtrl',
  ['$scope', '$rootScope',
    (scope, rootScope) ->
      rootScope.tabName = 'video'

  ]





