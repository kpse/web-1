'use strict'

angular.module('kulebaoAdmin')
.controller 'batchImportCtrl',
  [ '$scope', '$rootScope', '$stateParams', 'relationshipService',
    '$http', '$filter', '$q', 'classService', '$state', 'batchDataService', '$timeout', '$alert', 'phoneCheckService',
    'phoneCheckInSchoolService', 'childNameCheckService',
    (scope, rootScope, stateParams, Relationship, $http, $filter, $q, SchoolClass, $state, BatchData, $timeout, Alert,
     PhoneCheck, PhoneCheckInSchool, ChildNameCheck) ->
      scope.current_type = 'batchImport'

      parentRange = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']

      allParents = ->
        _.map parentRange, (r) -> "家长#{r}"

      extractGender = (input) ->
        if (input == '男') then 1 else 0

      cleanUpErrors = ->
        scope.importingErrorMessage = ''
        scope.errorItems = []
      resetData = ->
        scope.newClassesScope = []
        scope.relationships = []
        scope.fullRelationships = []
        scope.classesScope = []
        cleanUpErrors()
      backToImport = ->
        rootScope.loading = false
        $state.go 'kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: 'batchImport'}


      scope.onSuccess = (data) ->
        resetData()
        pickUpTheFirst = _.compose _.first, _.values
        scope.excel = pickUpTheFirst(data)
        scope.relationships = _.filter (_.flatten _.map scope.excel, (row, index) ->
          _.map allParents(), (p) ->
            new Relationship
              parent:
                name: row["#{p}姓名"]
                phone: row["#{p}手机号"]
                gender: extractGender row["#{p}性别"]
                birthday: '1980-01-01'
              child:
                class_name: row['所属班级']
                name: row['宝宝姓名']
                birthday: row['出生日期'] || '2010-01-01'
                gender: extractGender row['性别']
                address: row["家庭住址"]
              relationship: row["#{p}亲属关系"]
              school_id: parseInt(stateParams.kindergarten)
              index: index + 1
        ), (r) ->
          r.parent.name?

        rootScope.loading = true
        phones = _.pluck scope.relationships, 'parent.phone'

        if phones.length == 0
          scope.errorItems = []
          scope.importingErrorMessage = '数据格式不正确,请确认表格包含以下列名: 宝宝姓名,所属班级,性别,出生日期,家庭住址,家长A姓名,家长A性别,家长A亲属关系,家长A手机号'
          return backToImport()

        birthdayIssue = _.partition scope.relationships, (r) -> r.child.birthday? && r.child.birthday.match /\d{4}-\d{2}-\d{2}/
        if birthdayIssue[1].length > 0
          console.log birthdayIssue[1]
          scope.errorItems = _.map (_.zip _.pluck(birthdayIssue[1], 'child.name'), _.pluck(birthdayIssue[1], 'child.birthday')), (display) -> "#{display[0]} -> '#{display[1]}'"
          scope.importingErrorMessage = '以下学生生日格式不正确，请确保所有生日的格式都是2015-01-31。'
          return backToImport()

        noPhones = _.partition scope.relationships, (r) -> r.parent.phone? && r.parent.phone.length > 0
        if noPhones[1].length > 0
          console.log noPhones[1]
          scope.errorItems = _.map (_.zip _.pluck(noPhones[1], 'parent.name'), _.pluck(noPhones[1], 'child.name')), (display) -> "#{display[1]} -> #{display[0]}"
          scope.importingErrorMessage = '以下家长没有提供手机号，请修正后重新导入。'
          return backToImport()

        duplicatedChildName = _(scope.relationships).groupBy((r) -> r.child.name).filter((a) -> a.length > 1 && (_.uniq a, (item)->item.index).length > 1).value()
        if duplicatedChildName.length > 0
          console.log duplicatedChildName
          scope.errorItems = _.map duplicatedChildName, (display) -> "#{display[0].child.name} 分别出现在第 #{ _.uniq(_.pluck(display, 'index')).join(',')} 行。"
          scope.importingErrorMessage = '以下小孩名字重复，请修正后重新导入。'
          return backToImport()

        emptyNameChildren = _.filter scope.relationships, (r) -> !r.child.name? || r.child.name.length == 0
        if emptyNameChildren.length > 0
          console.log emptyNameChildren
          scope.errorItems = _.pluck(emptyNameChildren, 'parent.name')
          scope.importingErrorMessage = '以下家长的孩子名字为空，请检查调整后重新输入。'
          return backToImport()

        duplicatedPhones = _(scope.relationships).groupBy((r) -> r.parent.phone).filter((a) -> a.length > 1 && _.keys(_.groupBy a, 'parent.name').length > 1).value()
        if duplicatedPhones.length > 0
          console.log duplicatedPhones
          scope.errorItems = _.map duplicatedPhones, (display) -> "#{display[0].parent.phone} 同时被 #{ _.pluck(display, 'parent.name').join(',')} 持有。"
          scope.importingErrorMessage = '以下家长手机号重复但是姓名不一样，请修正后重新导入。'
          return backToImport()
        duplicatedNames = _(scope.relationships).groupBy((r) -> r.parent.name).filter((a) -> a.length > 1 && _.keys(_.groupBy a, 'parent.phone').length > 1).value()
        if duplicatedNames.length > 0
          console.log duplicatedNames
          scope.errorItems = _.map duplicatedNames, (display) -> "#{display[0].parent.name} 拥有号码 #{ _.pluck(display, 'parent.phone').join('\n,')} 。"
          scope.importingErrorMessage = '以下家长姓名重复但是手机号不一样，请修正后重新导入。'
          return backToImport()

        scope.newClassesScope = _.map (_.uniq _.map scope.relationships, (r) -> r.child.class_name), (c, i) ->
          name: c, class_id: i + 1000, school_id: parseInt(stateParams.kindergarten)

        nonExistingClasses = _.partition scope.newClassesScope, (c) -> _.findIndex(scope.kindergarten.classes, 'name', c.name) < 0
        if nonExistingClasses[0].length > 0
          console.log nonExistingClasses[0]
          scope.errorItems = _.pluck(nonExistingClasses[0], 'name')
          scope.importingErrorMessage = '以下班级当前并不存在，请检查调整后重新输入。'
          return backToImport()

        emptyClassChildren = _.filter scope.relationships, (r) -> !r.child.class_name? || r.child.class_name.length == 0
        if emptyClassChildren.length > 0
          console.log emptyClassChildren
          scope.errorItems = _.pluck(emptyClassChildren, 'child.name')
          scope.importingErrorMessage = '以下小孩没有分配班级，请检查调整后重新输入。'
          return backToImport()

        PhoneExistenceCheck = if scope.backend then PhoneCheck else PhoneCheckInSchool
        checkQueue = _.map phones, (p) -> PhoneExistenceCheck.check(phone: p, school_id: stateParams.kindergarten).$promise
        $q.all(checkQueue).then (q) ->
          codes = _.map q, (r) -> r.error_code
          scope.errorItems = _.uniq _.map (_.filter _.zip(phones, codes), (a) -> a[1] != 0), (p) -> p[0]
          if scope.errorItems.length > 0
            scope.importingErrorMessage = '以下手机号码已经存在，请检查调整后重新输入。'
            backToImport()
          else if scope.backend
            childNames = _.uniq _.pluck scope.relationships, 'child.name'
            checkQueue2 = _.map childNames, (n) -> ChildNameCheck.check(school_id: parseInt(stateParams.kindergarten), name: n).$promise
            $q.all(checkQueue2).then (q) ->
              codes = _.pluck q, 'error_code'
              scope.errorItems = _.map (_.filter _.zip(childNames, codes), (a) -> a[1] != 0), (p) -> p[0]
              rootScope.loading = false
              if scope.errorItems.length > 0
                scope.importingErrorMessage = '以下学生名字在学校中已经存在，无法判断是否同一学生，建议重名学生使用快速创建功能，避免信息冲突。'
                return backToImport()


        if scope.errorItems.length > 0 && scope.importingErrorMessage.length > 0
          backToImport()
        else if !scope.backend
          scope.classesScope = scope.newClassesScope
          SchoolClass.delete school_id: stateParams.kindergarten, ->
            classQueue = _.map scope.classesScope, (c) -> SchoolClass.save(c).$promise
            allClass = $q.all classQueue
            allClass.then (q) ->
              $state.go 'kindergarten.relationship.type.preview.class.list', {kindergarten: stateParams.kindergarten, type: 'batchImport', class_id: 1000}
        else
          scope.classesScope = scope.kindergarten.classes
          $state.go 'kindergarten.relationship.type.preview.class.list', {kindergarten: stateParams.kindergarten, type: 'batchImport', class_id: scope.kindergarten.classes[0].class_id}

        rootScope.loading = false

      BatchParents = BatchData('parents', stateParams.kindergarten)
      BatchChildren = BatchData('children', stateParams.kindergarten)
      BatchRelationship = BatchData('relationships', stateParams.kindergarten)

      classOfName = (name) ->
        _.find scope.classesScope, (c) ->
          c.name == name

      parentByName = (name) ->
        _.find scope.parents, (p) -> p.name == name

      childByName = (name) ->
        _.find scope.children, (c) -> c.name == name

      scope.extractChildren = (relationships, schoolId) ->
        _.map _.uniq(_.map(relationships, (r) -> r.child), (c) -> c.name)
        , (c, i) ->
          c.child_id = "2_#{schoolId}_#{new Date().getTime()}0#{i}"
          c.id = c.child_id
          c.school_id = schoolId
          c.nick = c.name
          c
      scope.extractParents = (relationships, schoolId) ->
        _.map _.uniq(_.map(relationships, (r) -> r.parent), (p) -> p.name)
        , (p, i) ->
          p.parent_id = "1_#{schoolId}_#{i}#{p.phone}"
          p.id = p.parent_id
          p.school_id = schoolId
          p

      assignIds = (relationships) ->
        schoolId = parseInt stateParams.kindergarten
        scope.parents = scope.extractParents scope.relationships, schoolId
        scope.children = scope.extractChildren scope.relationships, schoolId
        _.map relationships, (r, i) ->
          r.parent.id = parentByName(r.parent.name).id
          r.child.id = childByName(r.child.name).id
          r.child.class_id = classOfName(r.child.class_name).class_id
          r.card = ''
          r.id = "#{schoolId}0#{r.parent.id}0#{r.child.id}#{i}f".slice(-10)
          r
      compactRelationship = (list) ->
        _.map list, (r) ->
          id: r.id
          card: r.card
          parent:
            id: r.parent.id
          child:
            id: r.child.id
          relationship: r.relationship

      savingQueue = ->
        parentPromiseList = _.map (_.chunk scope.parents, 100), (l) -> BatchParents.save(l).$promise
        childPromiseList = _.map (_.chunk scope.children, 100), (l) -> BatchChildren.save(l).$promise
        parentPromiseList.concat childPromiseList

      scope.applyAllChange = ->
        rootScope.loading = true
        compactRelationships = compactRelationship(assignIds(scope.relationships))
        queue = savingQueue()
        allCreation = $q.all queue
        allCreation.then (q) ->
          if (_.every q, (f) -> f.error_code == 0)
            BatchRelationship.save compactRelationships, (q2) ->
              if q2.error_code == 0
                $timeout ->
                    $state.go('kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: 'connected'})
                  , 200
                $state.reload()
              else
                scope.importingErrorMessage = '批量导入关系失败，请检查调整后重新导入。'
                Alert
                  title: '批量导入失败'
                  content: scope.importingErrorMessage
                  placement: "top-left"
                  type: "danger"
                  container: '.panel-body'
                scope.errorItems = q2.error_msg
                rootScope.loading = false
                backToImport()
          else
            scope.importingErrorMessage = '批量创建家长和小孩失败，请检查调整后重新导入。'
            Alert
              title: '批量导入失败'
              content: scope.importingErrorMessage
              placement: "top-left"
              type: "danger"
              container: '.panel-body'
            scope.errorItems = _.pluck (_.filter q, (f) -> f.error_code != 0), 'error_msg'
            rootScope.loading = false
            backToImport()

      scope.navigateTo = (s) ->
        if stateParams.type != s.url
          rootScope.loading = true
          $state.go 'kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: s.url}

      scope.importConfirmMessage = '增量导入学生，家长和关系，你确定要导入数据的吗?'
      scope.importConfirmMessage = '批量导入会删除当前学校的所有数据,包括且不限于公告,家园互动和成长历史.你确定要应用新数据的吗?' unless scope.backend
      scope.importButtonTitle = '增量导入新数据'
      scope.importButtonTitle = '覆盖已有数据' unless scope.backend
      rootScope.loading = false
  ]

.controller 'ImportPreviewRelationshipCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$state', '$modal',
    (scope, rootScope, stateParams, $state, Modal) ->
      scope.current_class = parseInt stateParams.class_id
      schoolId = parseInt stateParams.kindergarten

      $state.go('kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: 'batchImport'}) unless scope.excel?

      classOfId = (id) ->
        _.find scope.classesScope, (c) ->
          c.class_id == parseInt id

      scope.relationships = _.filter scope.relationships, (r) ->
        r.child.class_name == classOfId(stateParams.class_id).name

      scope.children = scope.extractChildren scope.relationships, schoolId

      scope.navigateTo = (c) ->
        if c.class_id != scope.current_class
          rootScope.loading = true
          $state.go 'kindergarten.relationship.type.preview.class.list', {kindergarten: stateParams.kindergarten, type: 'batchImport', class_id: c.class_id}


      scope.previewChild = ->
        rootScope.loading = true
        scope.currentClass = classOfId(stateParams.class_id)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/children_preview.html'
          id: 'preview-child-modal'
        rootScope.loading = false

      rootScope.loading = false
  ]