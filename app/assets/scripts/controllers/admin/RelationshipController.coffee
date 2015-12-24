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

          scope.fetchFullRelationshipsIfNeeded(callback)

      scope.fetchFullRelationshipsIfNeeded = (callback)->
        scope.fullRelationships = scope.fullRelationships || []
        scope.fetching = scope.fetching || {$resolved : true}
        if scope.fullRelationships.length == 0 && scope.fetching.$resolved
          scope.fetching = Relationship.bind(school_id: stateParams.kindergarten).query (data) ->
            scope.fullRelationships = _.map data , (d) ->
              d.school_id = parseInt(stateParams.kindergarten)
              d
            callback(scope.fullRelationships) if callback?
            scope.$broadcast 'fullRelationships_ready', scope.fullRelationships
        else
          callback(scope.fullRelationships) if callback?

      scope.backend = true
      scope.refresh()

      scope.createParent = ->
        new Parent
          school_id: parseInt stateParams.kindergarten
          birthday: '1980-1-1'
          gender: 1
          name: ''
          member_status: 1
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
            _.each scope.parents, (p) ->
              p.validRelationships = possibleRelationship(p)
            scope.children = Child.query school_id: stateParams.kindergarten, ->
              scope.$emit 'clean_full_relationships'
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
          nick: fastCreating.child_name.slice(0, 4)
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
                    scope.$emit 'clean_full_relationships'
                    scope.refresh()
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
      scope.$on 'clean_full_relationships', ->
        scope.fullRelationships = []
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

      scope.$on 'fullRelationships_ready', (e, data) ->
#        console.log 'head counting: ' + JSON.stringify(scope.classesScope)
        groupedChildren = _.groupBy data, 'child.class_id'
        _.each scope.classesScope, (c) ->
          members = groupedChildren[c.class_id]
          headCount = if members? then members.length else 0
          $timeout ->
            c.highlightedCount = headCount + '人'
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
        scope.fetchFullRelationshipsIfNeeded (all) ->
          scope.relationships = _(all).filter((r) -> r.child.class_id == parseInt stateParams.class_id).map((r) ->
            _.assign extendFilterFriendlyProperties(r), school_id: parseInt stateParams.kindergarten).value()
          rootScope.loading = false

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
          scope.$emit 'clean_full_relationships'
          scope.refreshRelationship()
          scope.$emit 'sessionRead'

      scope.$on 'refreshing', ->
        scope.$emit 'clean_full_relationships'
        scope.refreshRelationship()

      scope.checkAll = (check) ->
        scope.relationships = _.map scope.relationships, (r) ->
          r.checked = check
          r

      scope.multipleDelete = ->
        checked = _.filter scope.relationships, (r) ->
          r.checked? && r.checked == true
        queue = _.map checked, (r) ->
          Relationship.delete(school_id: stateParams.kindergarten, card: r.card).$promise
        all = $q.all queue
        all.then (q) ->
          scope.$emit 'clean_full_relationships'
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
