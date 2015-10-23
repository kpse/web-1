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
          scope.types.pop() unless (scope.backend && scope.operatorOnly(scope.adminUser)) || !scope.backend
          scope.config =
            deletable : scope.adminUser.privilege_group == 'operator' || !scope.backend

          scope.fetchFullRelationshipsIfNeeded()
          callback() if callback?

      scope.fetchFullRelationshipsIfNeeded = ->
        scope.fullRelationships = scope.fullRelationships || []
        if scope.fullRelationships.length == 0
          Relationship.bind(school_id: stateParams.kindergarten).query (data) ->
            scope.fullRelationships = _.map data , (d) ->
              d.school_id = parseInt(stateParams.kindergarten)
              d

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

      scope.newParent = (saveHook, askForConnecting = true) ->
        rootScope.loading = true
        scope.parents = Parent.query school_id: stateParams.kindergarten, ->
          scope.parent = scope.createParent()
          scope.parent.saveHook = saveHook
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

      scope.saveParent = (parent) ->
        saveHook = parent.saveHook
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


      scope.updateVideoMember = (parent, save) ->
        if save
          VideoMember.save school_id: parent.school_id, id: parent.parent_id
        else
          VideoMember.remove school_id: parent.school_id, id: parent.parent_id

      scope.saveRelationship = (relationship) ->
        rootScope.loading = true
        relationship.$save ->
            scope.$broadcast 'refreshing'
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

      scope.createParentOnly = (child) ->
        scope.$broadcast 'refreshing'
        scope.currentModal.hide()
        doNotAskConnectAgain = false
        scope.newParent ((parent)->
          scope.newRelationship child, parent), doNotAskConnectAgain

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

      scope.updateCardNumber = (relationship) ->
        rootScope.loading = true
        relationship.$save ->
            scope.$broadcast 'refreshing'
            scope.refresh()
            scope.cardNumberEditing = 0
          , (res) ->
            handleError('关系', res)

      scope.navigateTo = (s) ->
        if stateParams.type != s.url
          rootScope.loading = true
          $state.go 'kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: s.url}

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
        newChild = _.assign scope.createChild(), {class_id: fastCreating.class_id, name: fastCreating.child_name}
        newParent = _.assign scope.createParent(), {name: fastCreating.parent_name, phone: fastCreating.phone}
        newRelationship = scope.createRelationship(newChild, newParent)
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
  ]

.controller 'connectedCtrl',
  ['$scope', '$rootScope', '$stateParams', 'accessClassService',
    (scope, rootScope, stateParams, AccessClass) ->
      scope.current_type = 'connected'

      AccessClass(scope.kindergarten.classes)

  ]

.controller 'ConnectedInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$state', '$timeout',
    'parentService', 'relationshipService',
    (scope, rootScope, stateParams, $state, $timeout, Parent, Relationship) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.navigateTo = (c) ->
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
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'relationshipService',
    '$http', '$filter', '$q', 'classService', '$state', 'batchDataService', '$timeout', '$alert', 'phoneCheckInSchoolService',
    (scope, rootScope, stateParams, location, Relationship, $http, $filter, $q, SchoolClass, $state, BatchData, $timeout, Alert, PhoneCheck) ->
      scope.current_type = 'batchImport'

      parentRange = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']

      allParents = ->
        _.map parentRange, (r) -> "家长#{r}"

      extractGender = (input) ->
        if (input == '男') then 1 else 0

      scope.onSuccess = (data) ->
        pickUpTheFirst = _.compose _.first, _.values
        scope.excel = pickUpTheFirst(data)
        scope.fullRelationships = []
        scope.relationships = []
        scope.classesScope = []
        scope.relationships = _.filter (_.flatten _.map scope.excel, (row) ->
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
                birthday: row['出生日期']
                gender: extractGender row['性别']
              relationship: row["#{p}亲属关系"]
              school_id: parseInt(stateParams.kindergarten)
        ), (r) ->
          r.parent.name?

        phones = _.map scope.relationships, (r) -> r.parent.phone
        checkQueue = _.map phones, (p) -> PhoneCheck.check(phone: p, school_id: parseInt(stateParams.kindergarten)).$promise
        $q.all(checkQueue).then (q) ->
          codes = _.map q, (r) -> r.error_code
          scope.errorNumbers = _.uniq _.map (_.filter _.zip(phones, codes), (a) -> a[1] != 0), (p) -> p[0]

        scope.newClassesScope = _.map (_.uniq _.map scope.relationships, (r) ->
            r.child.class_name)
        , (c, i) ->
          name: c, class_id: i + 1000, school_id: parseInt(stateParams.kindergarten)


        nonExistingClasses = _.partition scope.newClassesScope, (c) -> _.findIndex(scope.kindergarten.classes, 'name', c.name) < 0
        unless scope.backend
          scope.classesScope = scope.newClassesScope
          SchoolClass.delete school_id: stateParams.kindergarten, ->
            classQueue = _.map scope.classesScope, (c) -> SchoolClass.save(c).$promise
            allClass = $q.all classQueue
            allClass.then (q) ->
              location.path("kindergarten/#{stateParams.kindergarten}/relationship/type/batchImport/preview/class/1000/list")
        else if nonExistingClasses[0].length > 0
          console.log nonExistingClasses[0]
          scope.errorClassNames = _.pluck(nonExistingClasses[0], 'name')
          scope.importingErrorMessage = '以下班级当前并不存在“' + _.pluck(nonExistingClasses[0], 'name').join('”，“') + '”，请检查调整后重新输入。'
          Alert
          title: '批量导入失败'
          content: scope.importingErrorMessage
          placement: "top-left"
          type: "danger"
          container: '.panel-body'
        else
          scope.classesScope = scope.kindergarten.classes
          location.path("kindergarten/#{stateParams.kindergarten}/relationship/type/batchImport/preview/class/#{scope.kindergarten.classes[0].class_id}/list")


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
                 BatchChildren.save(scope.children).$promise,
                 BatchRelationship.save(compactRelationships).$promise]
        allCreation = $q.all queue
        allCreation.then (q) ->
          success = _.every q, (f) -> f.error_code == 0
          if success
            $timeout ->
                $state.go('kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: 'connected'})
              , 200
            $state.reload()
          else
            error = _.reduce (_.filter q, (f) -> f.error_code != 0),
                (all, err) -> all += "\n#{err.error_msg}"
              , ""
            Alert
              title: '批量导入失败'
              content: error
              placement: "top-left"
              type: "danger"
              container: '.panel-body'
            rootScope.loading = false

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
  ]
