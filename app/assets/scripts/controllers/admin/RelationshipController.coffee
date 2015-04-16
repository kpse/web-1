'use strict'

angular.module('kulebaoAdmin')
.controller 'RelationshipMainCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'parentService',
   'relationshipService', '$modal', 'childService', '$http', '$alert', 'videoMemberService', 'schoolConfigService',
    (scope, rootScope, stateParams, location, Parent, Relationship, Modal, Child, $http, Alert, VideoMember, SchoolConfig) ->
      rootScope.tabName = 'relationship'
      scope.heading = '管理幼儿及家长基础档案信息'

      scope.types = [
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

      scope.refresh = (callback)->
        scope.loading = true
        scope.disableMemberEditing = false
        SchoolConfig.get school_id: stateParams.kindergarten, (data)->
          backendConfig = _.find data['config'], (item) -> item.name == 'backend'
          backendConfig? && scope.backend = backendConfig.value == 'true'
          disableMemberEditingConfig = _.find data['config'], (item) -> item.name == 'disableMemberEditing'
          disableMemberEditingConfig? && scope.disableMemberEditing = disableMemberEditingConfig.value == 'true'
          scope.types.pop() if scope.backend or (!scope.backend and !scope.isSuperUser())
          scope.config =
            deletable : scope.adminUser.privilege_group == 'operator' || !scope.backend

        scope.relationships = Relationship.bind(school_id: stateParams.kindergarten).query ->
          callback() if callback?
          scope.loading = false

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

      scope.newParent = (saveHook, askForConnecting = true)->
        scope.parents = Parent.query school_id: stateParams.kindergarten, ->
          scope.parent = scope.createParent()
          scope.parent.saveHook = saveHook
          scope.connecting = askForConnecting
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_adult.html'

      scope.buttonLabel = '上传头像'
      scope.newChild = ->
        scope.nickFollowing = true
        scope.child = scope.createChild()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_child.html'

      scope.newRelationship = (child, parent)->
        scope.refresh ->
          scope.relationship = scope.createRelationship(child, parent)
          scope.parents = Parent.query school_id: stateParams.kindergarten, ->
            _.forEach scope.parents, (p) ->
              p.validRelationships = possibleRelationship(p)
            scope.children = Child.query school_id: stateParams.kindergarten, ->
              scope.currentModal = Modal
                scope: scope
                contentTemplate: 'templates/admin/add_connection.html'

      scope.editParent = (parent) ->
        scope.refresh ->
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

      scope.editChild = (child) ->
        scope.refresh ->
          scope.nickFollowing = true
          scope.child = Child.get school_id: stateParams.kindergarten, child_id: child.child_id, ->
            scope.currentModal = Modal
              scope: scope
              contentTemplate: 'templates/admin/add_child.html'

      scope.isCardDuplicated = (card) ->
        return false if card is undefined || card.length < 10
        undefined isnt _.find scope.relationships, (r) ->
          r.card == card

      handleError = (obj, res) ->
        Alert
          title: '保存' + obj + '失败'
          content: res.data.error_msg
          placement: "top"
          type: "danger"
          container: '.modal-dialog .panel-body'
          duration: 30

      scope.saveParent = (parent) ->
        saveHook = parent.saveHook
        video_member_status = parent.video_member_status
        parent.$save ->
          scope.updateVideoMember(parent, video_member_status == 1)
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()
          saveHook(parent) if typeof saveHook == 'function'
        , (res) ->
          handleError('家长', res)

      scope.updateVideoMember = (parent, save) ->
        if save
          VideoMember.save school_id: parent.school_id, id: parent.parent_id
        else
          VideoMember.remove school_id: parent.school_id, id: parent.parent_id

      scope.saveRelationship = (relationship) ->
        relationship.$save ->
          scope.$broadcast 'refreshing'
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError('关系', res)

      scope.saveChild = (child) ->
        child.$save ->
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()
        , (res) ->
          handleError('小孩', res)

      scope.alreadyConnected = (parent, child) ->
        return false if parent is undefined || child is undefined
        undefined isnt _.find scope.relationships, (r) ->
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
        scope.cardNumberEditing = r.card
        scope.oldRelationship = angular.copy r

      scope.cancelEditing = (r)->
        scope.cardNumberEditing = 0
        r.card = scope.oldRelationship.card

      scope.updateCardNumber = (relationship) ->
        relationship.$save ->
          scope.$broadcast 'refreshing'
          scope.refresh()
          scope.cardNumberEditing = 0
        , (res) ->
          handleError('关系', res)

      scope.navigateTo = (s) ->
        location.path("kindergarten/#{stateParams.kindergarten}/relationship/type/#{s.url}") if stateParams.type != s.url

      scope.membersInformation =
        members : [{id: 0, desc:'未开通'}, {id: 1, desc: '已开通'}]
        videoMembers : [{id: 0, desc:'未开通'}, {id: 1, desc: '已开通'}]
  ]

.controller 'connectedCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'accessClassService',
    (scope, rootScope, stateParams, location, AccessClass) ->
      scope.current_type = 'connected'

      AccessClass(scope.kindergarten.classes)

      scope.navigateTo = (s) ->
        location.path("kindergarten/#{stateParams.kindergarten}/relationship/type/#{s.url}") if stateParams.type != s.url
  ]

.controller 'ConnectedInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'parentService', 'relationshipService',
    (scope, rootScope, stateParams, location, Parent, Relationship) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.loading = true
      scope.relationships = Relationship.bind(school_id: stateParams.kindergarten).query ->
        scope.loading = false

      scope.navigateTo = (c) ->
        location.path("kindergarten/#{stateParams.kindergarten}/relationship/type/#{stateParams.type}/class/#{c.class_id}/list")

  ]

.controller 'ConnectedRelationshipCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'relationshipService',
    '$http', '$filter', '$q',
    (scope, rootScope, stateParams, location, Relationship, $http, $filter, $q) ->
      extendFilterFriendlyProperties = (r) ->
        r.phone = r.parent.phone
        r.formattedPhone = $filter('phone')(r.parent.phone)
        r.childName = r.child.name
        r.className = r.child.class_name
        r

      scope.refreshRelationship = ->
        scope.loading = true
        relationships = Relationship.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id).query ->
          scope.relationships = _.map relationships, (r) ->
            extendFilterFriendlyProperties(r)
          scope.loading = false

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
        data = {"school_id":parseInt(stateParams.kindergarten),"driver_id":mockDriverId(),"latitude":108.883425,"longitude":134.253351,"direction":180.89999389648438,"radius":0.8999999761581421,"address":"地球1"}
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
          scope.refreshRelationship()

      scope.$on 'refreshing', ->
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
    (scope, rootScope, stateParams, location, Relationship, $http, $filter, $q, School, $state, BatchData, $timeout, Alert, PhoneCheck) ->
      scope.loading = false
      scope.current_type = 'batchImport'

      parentRange = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']

      allParents = ->
        _.map parentRange, (r) ->
          "家长#{r}"

      extractGender = (input) ->
        if (input == '男') then 1 else 0

      scope.onSuccess = (data)->
        scope.excel = data
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

        scope.classesScope = _.map (_.uniq _.map scope.relationships, (r) ->
            r.child.class_name)
        , (c, i) ->
          name: c, class_id: i + 1000, school_id: parseInt(stateParams.kindergarten)

        location.path("kindergarten/#{stateParams.kindergarten}/relationship/type/batchImport/preview/class/1000/list")

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
          r.card = "#{schoolId}0#{schoolId}0#{schoolId}#{i}f".slice(-10);
          r.id = r.card
          r

      scope.applyAllChange = ->
        scope.loading = true
        School.delete school_id: stateParams.kindergarten, ->
          classQueue = _.map scope.classesScope, (c) ->
            School.save(c).$promise
          allClass = $q.all classQueue
          allClass.then (q) ->
            scope.relationships = assignIds(scope.relationships)
            queue = [BatchParents.save(scope.parents).$promise,
                     BatchChildren.save(scope.children).$promise,
                     BatchRelationship.save(scope.relationships).$promise]
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
                scope.loading = false

  ]

.controller 'ImportPreviewRelationshipCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', '$state', '$modal',
    (scope, rootScope, stateParams, location, $state, Modal) ->
      scope.loading = false
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
        location.path("kindergarten/#{stateParams.kindergarten}/relationship/type/batchImport/preview/class/#{c.class_id}/list")

      scope.previewChild = ->
        scope.currentClass = classOfId(stateParams.class_id)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/children_preview.html'
          id: 'preview-child-modal'
  ]
