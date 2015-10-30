'use strict'

angular.module('kulebaoAdmin')
.controller 'RelationshipMainCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', 'parentService',
   'relationshipService', '$modal', 'childService', '$http', '$alert', 'videoMemberService', 'schoolConfigService', 'schoolConfigExtractService',
    (scope, rootScope, stateParams, $state, Parent, Relationship, Modal, Child, $http, Alert, VideoMember, SchoolConfig, ConfigExtract) ->
      rootScope.tabName = 'relationship'
      scope.heading = '管理幼儿及家长基础档案信息'

      rootScope.loading = true

      scope.allTypes = -> [
        {
          name: '已关联'
          url: 'connected'
        },
        {
          name: '未关联家长'
          url: 'unconnectedParent'
        },
        {
          name: '未关联小孩'
          url: 'unconnectedChild'
        },
        {
          name: '批量导入'
          url: 'batchImport'
        }
      ]

      scope.refresh = (callback) ->
        rootScope.loading = true
        scope.disableMemberEditing = false
        SchoolConfig.get school_id: stateParams.kindergarten, (data) ->
          backendConfig = _.find data['config'], (item) -> item.name == 'backend'
          backendConfig? && scope.backend = backendConfig.value == 'true'
          disableMemberEditingConfig = _.find data['config'], (item) -> item.name == 'disableMemberEditing'
          disableMemberEditingConfig? && scope.disableMemberEditing = disableMemberEditingConfig.value == 'true'
          scope.videoTrialAccount = ConfigExtract data['config'], 'videoTrialAccount'
          scope.types = scope.allTypes()
          scope.config =
            deletable : scope.adminUser.privilege_group == 'operator' || !scope.backend

          scope.fetchFullRelationshipsIfNeeded()
          callback() if callback?

      scope.fetchFullRelationshipsIfNeeded = (callback)->
        scope.fullRelationships = scope.fullRelationships || []
        if scope.fullRelationships.length == 0
          Relationship.bind(school_id: stateParams.kindergarten).query (data) ->
            scope.fullRelationships = _.map data , (d) ->
              d.school_id = parseInt(stateParams.kindergarten)
              d
            callback(scope.fullRelationships) if callback?

      scope.backend = true
      scope.refresh()

      scope.createParent = ->
        new Parent
          school_id: parseInt stateParams.kindergarten
          birthday: '1980-1-1'
          gender: 1
          name: ''
          member_status: 0
          kindergarten: scope.kindergarten
          video_member_status: 0

      scope.createChild = ->
        new Child
          name: ''
          nick: ''
          birthday: '2009-1-1'
          gender: 1
          class_id: scope.kindergarten.classes[0].class_id
          school_id: parseInt stateParams.kindergarten

      possibleRelationship = (person) ->
        {0: ['妈妈', '奶奶', '姥姥'], 1: ['爸爸', '爷爷', '姥爷']}[person.gender]
      recommend = (parent) ->
        if parent? && parent.validRelationships?
          parent.validRelationships[0]
        else
          '妈妈'

      scope.createRelationship = (child, parent)->
        parent.validRelationships = possibleRelationship(parent) if parent?
        new Relationship
          school_id: parseInt stateParams.kindergarten
          relationship: recommend(parent)
          child: child
          parent: parent
          fix_child: child?
          fix_parent: parent?

      scope.newParent = (saveHook, fastSaveHook, askForConnecting = true) ->
        rootScope.loading = true
        scope.parents = Parent.query school_id: stateParams.kindergarten, ->
          scope.parent = scope.createParent()
          scope.parent.saveHook = saveHook
          scope.parent.fastSaveHook = fastSaveHook
          scope.connecting = askForConnecting
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_adult.html'
          rootScope.loading = false

      scope.buttonLabel = '上传头像'
      scope.newChild = ->
        scope.nickFollowing = true
        scope.child = scope.createChild()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_child.html'

      scope.newRelationship = (child, parent)->
        rootScope.loading = true
        scope.refresh ->
          rootScope.loading = true
          scope.relationship = scope.createRelationship(child, parent)
          scope.parents = Parent.query school_id: stateParams.kindergarten, ->
            _.forEach scope.parents, (p) ->
              p.validRelationships = possibleRelationship(p)
            scope.children = Child.query school_id: stateParams.kindergarten, ->
              scope.fullRelationships = []
              scope.fetchFullRelationshipsIfNeeded ->
                scope.currentModal = Modal
                  scope: scope
                  contentTemplate: 'templates/admin/add_connection.html'
                rootScope.loading = false


      scope.editParent = (parent) ->
        scope.refresh ->
          rootScope.loading = true
          scope.parent = Parent.get school_id: stateParams.kindergarten, phone: parent.phone, (p) ->
            scope.parent.video_member_status = 0
            VideoMember.get school_id: stateParams.kindergarten, id: parent.parent_id, ->
                p.video_member_status = 1
              , (e) ->
                p.video_member_status = 0
            scope.connecting = true
            scope.currentModal = Modal
              scope: scope
              contentTemplate: 'templates/admin/add_adult.html'
            rootScope.loading = false

      scope.editChild = (child) ->
        scope.refresh ->
          scope.nickFollowing = true
          scope.child = Child.get school_id: stateParams.kindergarten, child_id: child.child_id, ->
            scope.currentModal = Modal
              scope: scope
              contentTemplate: 'templates/admin/add_child.html'

      scope.isCardDuplicated = (card) ->
        return false if card is undefined
        _.any scope.fullRelationships, (r) -> r.card == card

      handleError = (obj, res) ->
        Alert
          title: '保存' + obj + '失败'
          content: res.data.error_msg
          placement: "top"
          type: "danger"
          container: '.modal-dialog .panel-body'
          duration: 3
        rootScope.loading = false

      saveParentWithHook = (parent, saveHook) ->
        video_member_status = parent.video_member_status
        rootScope.loading = true
        parent.$save ->
            scope.updateVideoMember(parent, video_member_status == 1)
            scope.$broadcast 'refreshing'
            scope.currentModal.hide()
            saveHook(parent) if typeof saveHook == 'function'
            rootScope.loading = false
          , (res) ->
            handleError('家长', res)
            rootScope.loading = false

      scope.saveParent = (parent) ->
        saveHook = parent.saveHook
        saveParentWithHook(parent, saveHook)

      scope.fastConnectToParent = (parent) ->
        saveHook = parent.fastSaveHook
        saveParentWithHook(parent, saveHook)

      scope.createParentOnly = (child) ->
        scope.$broadcast 'refreshing'
        scope.currentModal.hide()
        doNotAskConnectAgain = false
        saveHook = (parent)->
          scope.newRelationship child, parent
        fastSaveHook = (parent)->
          fastRelationship = scope.createRelationship(child, parent)
          scope.saveRelationship fastRelationship
        scope.newParent saveHook, fastSaveHook, doNotAskConnectAgain

      scope.updateVideoMember = (parent, save) ->
        if save
          VideoMember.save school_id: parent.school_id, id: parent.parent_id
        else
          VideoMember.remove school_id: parent.school_id, id: parent.parent_id

      scope.saveRelationship = (relationship) ->
        rootScope.loading = true
        relationship.$save ->
            scope.$broadcast 'refreshing'
            scope.$emit 'sessionRead'
            scope.currentModal.hide()
            rootScope.loading = false
          , (res) ->
            handleError('关系', res)
            rootScope.loading = false

      scope.saveChild = (child) ->
        rootScope.loading = true
        child.$save ->
            scope.$broadcast 'refreshing'
            scope.currentModal.hide()
            rootScope.loading = false
          , (res) ->
            handleError('小孩', res)
            rootScope.loading = false

      scope.alreadyConnected = (parent, child) ->
        return false if parent is undefined || child is undefined
        _.any scope.fullRelationships, (r) ->
          r.parent.phone == parent.phone && r.child.child_id == child.child_id

      scope.availableChildFor = (parent) ->
        if parent is undefined
          scope.children
        else
          _.reject scope.children, (c) ->
            scope.alreadyConnected(parent, c)

      scope.availableParentFor = (child) ->
        if child is undefined
          scope.parents
        else
          _.reject scope.parents, (p) ->
            scope.alreadyConnected(p, child)

      scope.createParentFor = (child) ->
        child.$save ->
          scope.createParentOnly child

      scope.connectToExists = (child, parent) ->
        child.$save ->
          scope.connectToExistingOnly child, parent

      scope.connectToChild = (parent) ->
        video_member_status = parent.video_member_status
        parent.$save ->
            scope.updateVideoMember(parent, video_member_status)
            scope.connectToChildOnly(parent)
          , (res) ->
            handleError('家长', res)

      scope.connectToExistingOnly = (child, parent) ->
        scope.$broadcast 'refreshing'
        scope.currentModal.hide()
        scope.newRelationship child, parent

      scope.connectToChildOnly = (parent) ->
        scope.$broadcast 'refreshing'
        scope.currentModal.hide()
        scope.newRelationship undefined, parent


      scope.nameChanging = ->
        scope.child.nick = scope.child.name if scope.nickFollowing && scope.child.name && scope.child.name.length < 5

      scope.stopFollowing = ->
        scope.nickFollowing = false

      scope.parentChange = (r) ->
        if !_.contains r.parent.validRelationships, r.relationship
          r.relationship = r.parent.validRelationships[0]

      scope.cardNumberEditing = 0
      scope.editing = (r) ->
        scope.oldRelationship = angular.copy r
        r.card = '0000000000' if scope.invalidCard(r)
        scope.cardNumberEditing = r.id

      scope.cancelEditing = (r)->
        scope.cardNumberEditing = 0
        r.card = scope.oldRelationship.card

      scope.cancelDialog = ->
        scope.currentModal.hide()
        rootScope.loading = false

      scope.updateCardNumber = (relationship) ->
        rootScope.loading = true
        relationship.$save ->
            scope.$broadcast 'refreshing'
            scope.refresh()
            scope.cardNumberEditing = 0
          , (res) ->
            handleError('关系', res)

      scope.membersInformation =
        members : [{id: 0, desc:'未开通'}, {id: 1, desc: '已开通'}]
        videoMembers : [{id: 0, desc:'未开通'}, {id: 1, desc: '已开通'}]

      scope.invalidCard = (r) -> !r.card? or 'f' == _.first r.card

      scope.fastCreate = ->
        scope.fastCreation = scope.createFastCreation()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/fast_creating.html'

      scope.saveFastCreating = (fastCreating)->
        scope.saving = true
        newChild = _.assign scope.createChild(), {
          class_id: fastCreating.class_id
          name: fastCreating.child_name
          gender: fastCreating.gender
          birthday: fastCreating.birthday
        }
        newParent = _.assign scope.createParent(), {name: fastCreating.parent_name, phone: fastCreating.phone}
        newRelationship = scope.createRelationship(newChild, newParent)
        newRelationship.relationship = fastCreating.relationship
        newChild.$save ->
            newParent.$save ->
                newRelationship.$save ->
                    scope.currentModal.hide()
                    scope.$broadcast 'refreshing'
                    scope.saving = false
                  , (res) ->
                    handleError('关系', res)
              , (res) ->
                handleError('家长', res)
          , (res) ->
            handleError('学生', res)

      scope.createFastCreation = ->
        class_id : scope.kindergarten.classes[0].class_id
        child_name : ''
        parent_name : ''
        phone : ''
        relationship : ''
        gender : 0
        birthday : '2010-01-01'

      scope.cardDisplay = (card) ->
        if 'f' == _.first card
          ''
        else
          card

      scope.noCard = false
      scope.toggleFakeCard = (form) ->
        scope.noCard = !scope.noCard
        cleanUpCardErrors(form)

      cleanUpCardErrors = (form) ->
        scope.relationship.card = ''
        form.$setPristine()
        form.card.$setValidity "registered", true
        rootScope.loading = false
  ]

.controller 'connectedCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', 'accessClassService',
    (scope, rootScope, stateParams, $state, AccessClass) ->
      scope.current_type = 'connected'

      AccessClass(scope.kindergarten.classes)

      scope.navigateTo = (s) ->
        if stateParams.type != s.url
          rootScope.loading = true
          $state.go 'kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: s.url}
  ]

.controller 'ConnectedInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$state', '$timeout',
    (scope, rootScope, stateParams, $state, $timeout) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.navigateTo = (c) ->
        if scope.current_class != c.class_id || !$state.is 'kindergarten.relationship.type.class.list'
          rootScope.loading = true
          $timeout ->
            $state.go 'kindergarten.relationship.type.class.list', {kindergarten: stateParams.kindergarten, type: stateParams.type, class_id: c.class_id}

  ]

.controller 'ConnectedRelationshipCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$location', 'relationshipService', '$http', '$filter', '$q',
    (scope, rootScope, stateParams, location, Relationship, $http, $filter, $q) ->
      extendFilterFriendlyProperties = (r) ->
        r.phone = r.parent.phone
        r.formattedPhone = $filter('phone')(r.parent.phone)
        r.childName = r.child.name
        r.className = r.child.class_name
        r

      scope.refreshRelationship = ->
        rootScope.loading = true
        Relationship.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id).query (data) ->
          scope.relationships = _.map data, (r) ->
            _.assign extendFilterFriendlyProperties(r), school_id: parseInt stateParams.kindergarten
          rootScope.loading = false
        scope.fetchFullRelationshipsIfNeeded()

      scope.refreshRelationship()

      generateChildCheckingInfo = (child, type) ->
        school_id: parseInt(stateParams.kindergarten)
        child_id: child.child_id
        check_type: type
        timestamp: new Date().getTime()

      generateCheckingInfo = (card, name, type) ->
        school_id: parseInt(stateParams.kindergarten)
        card_no: card
        card_type: 2
        notice_type: type
        record_url: 'https://dn-cocobabys.qbox.me/big_shots.jpg'
        parent: name
        timestamp: new Date().getTime()

      scope.sendMessage = (relationship, type) ->
        check = generateCheckingInfo(relationship.card, relationship.parent.name, type)
        $http({method: 'POST', url: '/kindergarten/' + stateParams.kindergarten + '/check', data: check}).success (data) ->
          alert 'error_code:' + data.error_code

      mockDriverId = -> '3_2088_1427118225700'

      host = ->
        if location.host() == 'localhost' then "http://localhost:9000" else "#{location.protocol()}://#{location.host()}"

      mockDriverLocationReport = ->
        data = {"school_id":parseInt(stateParams.kindergarten),"driver_id":mockDriverId(),"latitude":30.739469,"longitude":104.179257,"direction":180.89999389648438,"radius":0.8999999761581421,"address":"地球1"}
        $http {method: 'POST', url: "/api/v2/kindergarten/#{stateParams.kindergarten}/bus_driver/#{mockDriverId()}/location", data: data}

      scope.sendBusMessage = (relationship, type) ->
        check = switch type
          when 10,11 then generateCheckingInfo(relationship.card, relationship.parent.name, type)
          when 20,21 then generateChildCheckingInfo(relationship.child, type)

        method = switch type
          when 10 then 'check_out'
          when 11 then 'check_in'
          when 20 then 'child_on_bus'
          when 21 then 'child_off_bus'
          else ''

        mockDriverLocationReport()
        $http({method: 'POST', url: "/api/v2/kindergarten/#{stateParams.kindergarten}/bus_driver/#{mockDriverId()}/#{method}", data: check}).success (data) ->
          alert "bus driver is : 3_2088_1427118225700, check child status by url: #{host()}/api/v2/kindergarten/#{stateParams.kindergarten}/last_bus_location/#{relationship.child.child_id}"

      scope.delete = (card) ->
        Relationship.delete school_id: stateParams.kindergarten, card: card, ->
          scope.fullRelationships = []
          scope.refreshRelationship()
          scope.$emit 'sessionRead'

      scope.$on 'refreshing', ->
        scope.fullRelationships = []
        scope.refreshRelationship()

      scope.checkAll = (check) ->
        _.forEach scope.relationships, (r) ->
          r.checked = check

      scope.multipleDelete = ->
        checked = _.filter scope.relationships, (r) ->
          r.checked? && r.checked == true
        queue = _.map checked, (r) ->
          Relationship.delete(school_id: stateParams.kindergarten, card: r.card).$promise
        all = $q.all queue
        all.then (q) ->
          scope.fullRelationships = []
          scope.refreshRelationship()
          scope.$emit 'sessionRead'

      scope.hasSelection = (relationships) ->
        _.some relationships, (r) ->
          r.checked? && r.checked == true

      scope.singleSelection = (relationship) ->
        allChecked = _.every scope.relationships, (r) ->
          r.checked? && r.checked == true
        scope.selection.allCheck = allChecked

      scope.selection =
        allCheck: false
  ]

.controller 'batchImportCtrl',
  [ '$scope', '$rootScope', '$stateParams', 'relationshipService',
    '$http', '$filter', '$q', 'classService', '$state', 'batchDataService', '$timeout', '$alert', 'phoneCheckService', 'phoneCheckInSchoolService',
    (scope, rootScope, stateParams, Relationship, $http, $filter, $q, SchoolClass, $state, BatchData, $timeout, Alert, PhoneCheck, PhoneCheckInSchool) ->
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
        scope.relationships = []
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
              relationship: row["#{p}亲属关系"]
              school_id: parseInt(stateParams.kindergarten)
              index: index + 1
        ), (r) ->
          r.parent.name?

        rootScope.loading = true
        phones = _.map scope.relationships, (r) -> r.parent.phone

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

        PhoneExistenceCheck = if scope.backend then PhoneCheck else PhoneCheckInSchool
        checkQueue = _.map phones, (p) -> PhoneExistenceCheck.check(phone: p).$promise
        $q.all(checkQueue).then (q) ->
          codes = _.map q, (r) -> r.error_code
          scope.errorItems = _.uniq _.map (_.filter _.zip(phones, codes), (a) -> a[1] != 0), (p) -> p[0]
          scope.importingErrorMessage = '以下手机号码已经存在，请检查调整后重新输入。' if scope.errorItems.length > 0
          backToImport() if scope.errorItems.length > 0
          rootScope.loading = false

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
          c.child_id = "2_#{schoolId}_00#{i}"
          c.id = c.child_id
          c.school_id = schoolId
          c.nick = c.name
          c
      scope.extractParents = (relationships, schoolId) ->
        _.map _.uniq(_.map(relationships, (r) -> r.parent), (p) -> p.name)
        , (p, i) ->
          p.parent_id = "1_#{schoolId}_00#{i}"
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

      scope.applyAllChange = ->
        rootScope.loading = true
        compactRelationships = compactRelationship(assignIds(scope.relationships))
        queue = [BatchParents.save(scope.parents).$promise,
                 BatchChildren.save(scope.children).$promise]
        allCreation = $q.all queue
        allCreation.then (q) ->
          if (_.every q, (f) -> f.error_code == 0)
            BatchRelationship.save compactRelationships, (q2) ->
              if q2.error_code == 0
                $timeout ->
                    $state.go('kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: 'connected'})
                  , 200
                scope.$emit 'sessionRead'
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
