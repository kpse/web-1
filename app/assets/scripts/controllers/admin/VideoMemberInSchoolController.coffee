angular.module('kulebaoAdmin').controller 'VideoMemberManagementCtrl',
  ['$scope', '$rootScope', '$state', '$stateParams', '$q', '$filter', 'chargeService', 'videoMemberService',
   'senderService', 'relationshipService', 'classService', 'parentService', '$modal',
    (scope, rootScope, $state, $stateParams, $q, $filter, Charge, VideoMember, Parent, Relationship, Class, ParentSearch, Modal) ->
      rootScope.tabName = 'video'
      scope.loading = true
      extendFilterFriendlyProperties = (p) ->
        p.phone = p.detail.phone
        p.formattedPhone = $filter('phone')(p.detail.phone)
        p

      Class.query school_id: $stateParams.kindergarten, (data)->
        scope.kindergarten =
          classes : data
        scope.current_class = parseInt $state.params.class if $state.params.class?
        scope.current_class = scope.kindergarten.classes[0].class_id unless scope.current_class?
        queue = [Charge.query(school_id: $stateParams.kindergarten).$promise
                 VideoMember.query(school_id: $stateParams.kindergarten).$promise]
        all = $q.all(queue)
        all.then (q) ->
          scope.charge = q[0]
          [defaultAccounts, parents] = _.partition q[1], (a) -> a.id == 'default'
          parents = _.map parents, (p) ->
            p.promise =
              $q (resolve, reject) ->
                Parent.get school_id: $stateParams.kindergarten, id: p.id, type: 'p', (data)->
                    p.detail = data
                    if p.detail.phone?
                      p.reltaionship = Relationship.query school_id: $stateParams.kindergarten, parent: p.detail.phone, ->
                          resolve()
                        , -> resolve()
                      extendFilterFriendlyProperties(p)
                    else
                      resolve()
                  , -> resolve()
            p

          defaultAccounts = _.map defaultAccounts, (d) ->
            d.detail =
              name : '试用账号'
              phone :'试用账号'
            d
          $state.go 'kindergarten.video_default', kindergarten: $stateParams.kindergarten if defaultAccounts.length > 0
          scope.parents = _.flatten [parents, defaultAccounts]
          allLoading = _.map scope.parents, (p) -> p.promise
          $q.all(allLoading).then (q) ->
            scope.refresh()
            scope.loading = false

      scope.refresh = (clz = scope.current_class) ->
        scope.parentsInClass = _.filter scope.parents, (c) -> scope.display(c, clz)

      scope.display = (p, classId) ->
        p.reltaionship? && p.reltaionship.$resolved && p.reltaionship.length > 0 && p.reltaionship[0].child.class_id == classId

      scope.accountsInSchool = (parents, classId) ->
        _.map (_.filter parents, (f) -> !classId? || scope.display(f, classId)), (p) ->
          'account': p.account,
          'password': p.password,
          'name': p.detail.phone + p.detail.name

      phoneFieldName = '家长手机号'
      nameFieldName = '家长姓名'
      classFieldName = '班级'
      childFieldName = '学生姓名'

      validateData = (data) ->
        data? && data.length > 0 && _.all data, (d) -> _.all [phoneFieldName, nameFieldName, classFieldName, childFieldName], (n) -> _.has d, n

      pickUpCorrectData = (rawData) ->
        _.find rawData, validateData


      scope.onSuccess = (rawData)->
        importingData = pickUpCorrectData(rawData)
        return alert("导入文件格式错误，每行至少要包含‘#{phoneFieldName}’，‘#{nameFieldName}’， ‘#{classFieldName}’，‘#{childFieldName}’列，请检查excel内容。") unless importingData?
        queue = [ParentSearch.query(school_id: $stateParams.kindergarten).$promise
          Relationship.query(school_id: $stateParams.kindergarten).$promise]
        all = $q.all(queue)
        all.then (q) ->
          group = _.groupBy q[0], 'phone'
          relationshipGroup = _.groupBy q[1], 'parent.phone'
          importingData = _.map importingData, (d, i) ->
            d.name = d[nameFieldName]
            d.className = d[classFieldName]
            d.phone = d[phoneFieldName]
            d.childName = d[childFieldName]
            d.index = i + 1
            d

          [scope.importingData, scope.errorDataNoPhone] = _.partition importingData, (d) -> _.has group, d.phone

          scope.errorDataNoPhone = _.map scope.errorDataNoPhone, (d) -> d.error = '无此手机号码或手机号码错误。';d

          [scope.importingData, scope.errorDataWrongName] = _.partition scope.importingData, (d) ->
            group[d.phone]? && group[d.phone][0].name == d.name

          scope.importingData = _.map scope.importingData, (d) -> d.detail = group[d.phone][0];d

          scope.errorDataWrongName = _.map scope.errorDataWrongName, (d) -> d.error = '家长姓名不符。';d

          [scope.importingData, scope.errorDataNoConnection] = _.partition scope.importingData, (d) -> _.has relationshipGroup, d.phone

          scope.importingData = _.map scope.importingData, (d) -> d.relationship = relationshipGroup[d.phone];d

          scope.errorDataNoConnection = _.map scope.errorDataNoConnection, (d) -> d.error = '家长没有关联的学生。';d

          [scope.importingData, scope.errorDataErrorChildName] = _.partition scope.importingData, (d) ->
            relationshipGroup[d.phone]? && _.any relationshipGroup[d.phone], (r) -> r.child.name == d.childName

          scope.errorDataErrorChildName = _.map scope.errorDataErrorChildName, (d) -> d.error = '学生姓名不符。';d

          [scope.importingData, scope.errorDataClassNotMatch] = _.partition scope.importingData, (d) ->
            relationshipGroup[d.phone]? && _.any relationshipGroup[d.phone], (r) -> r.child.class_name == d.className

          scope.errorDataClassNotMatch = _.map scope.errorDataClassNotMatch, (d) -> d.error = '班级不符。';d

          [scope.enabledData, scope.importingData] = _.partition scope.importingData, (d) ->
            _.any scope.parents, (p) -> p.phone == d.phone

          scope.enabledData = _.map scope.enabledData, (d) -> d.error = "该号码#{d.phone}已经开通看宝宝。";d

          scope.errorData = _.flatten [scope.errorDataNoPhone, scope.errorDataWrongName, scope.errorDataNoConnection,
                                       scope.errorDataClassNotMatch, scope.errorDataErrorChildName]

          scope.newParentsInClass = []
          if scope.errorData.length > 0
            scope.currentModal = Modal
              scope: scope
              contentTemplate: 'templates/admin/video_import_warning.html'
          else if scope.importingData.length == 0 && scope.enabledData.length > 0
            alert 'excel中所有家长均已开通看宝宝服务，请检查输入数据'
            scope.cancelImporting()
          else if scope.importingData.length == 0
            alert '没有可用的数据，请检查输入数据'
            scope.cancelImporting()
          else
            scope.importingClasses = angular.copy scope.kindergarten.classes
            scope.navigateToImportingClass(scope.importingClasses[0].name)


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
          $state.go $state.current,
              class: scope.current_class
            , reload: true
          scope.loading = false

      scope.navigateTo = (clz) ->
        scope.current_class = clz.class_id
        scope.refresh()

      scope.navigateToImportingClass = (clz) ->
        scope.currentImportingClass = clz
        scope.newParentsInClass = _.filter scope.importingData, (c) -> c.className == clz

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





