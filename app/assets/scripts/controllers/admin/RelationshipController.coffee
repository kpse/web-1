'use strict'

angular.module('kulebaoAdmin')
.controller 'RelationshipMainCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService', 'parentService',
   'relationshipService', '$modal', 'childService', '$http', '$alert', 'uploadService', 'employeeService'
    (scope, rootScope, stateParams, location, School, Class, Parent, Relationship, Modal, Child, $http, Alert, Upload, Employee) ->
      rootScope.tabName = 'relationship'
      scope.heading = '管理幼儿及家长基础档案信息'

      scope.adminUser = Employee.get()

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
        }
      ]

      scope.refresh = ->
        scope.loading = true
        scope.relationships = Relationship.bind(school_id: stateParams.kindergarten).query ->
          scope.loading = false

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
          scope.refresh()

      scope.createParent = ->
        new Parent
          school_id: parseInt stateParams.kindergarten
          birthday: '1980-1-1'
          gender: 1
          name: ''
          member_status: 0
          kindergarten: scope.kindergarten

      scope.createChild = ->
        new Child
          name: ''
          nick: ''
          birthday: '2009-1-1'
          gender: 1
          class_id: scope.kindergarten.classes[0].class_id
          school_id: parseInt stateParams.kindergarten

      recommand = (parent) ->
        if parent? && parent.validRelationships?
          parent.validRelationships[0]
        else
          '妈妈'

      scope.createRelationship = (child, parent)->
        parent.validRelationships = {0: ['妈妈', '奶奶', '姥姥'], 1: ['爸爸', '爷爷', '姥爷']}[parent.gender] if parent?
        new Relationship
          school_id: parseInt stateParams.kindergarten
          relationship: recommand(parent)
          child: child
          parent: parent
          fix_child: child?
          fix_parent: parent?

      scope.newParent = (saveHook)->
        scope.parents = Parent.query school_id: stateParams.kindergarten, ->
          scope.parent = scope.createParent()
          scope.parent.saveHook = saveHook
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_adult.html'

      scope.newChild = ->
        scope.nickFollowing = true
        scope.child = scope.createChild()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_child.html'

      scope.newRelationship = (child, parent)->
        scope.relationship = scope.createRelationship(child, parent)
        scope.parents = Parent.query school_id: stateParams.kindergarten, ->
          _.forEach scope.parents, (p) -> p.validRelationships = {0: ['妈妈', '奶奶', '姥姥'], 1: ['爸爸', '爷爷', '姥爷']}[p.gender]
          scope.children = Child.query school_id: stateParams.kindergarten, ->
            scope.currentModal = Modal
              scope: scope
              contentTemplate: 'templates/admin/add_connection.html'

      scope.editParent = (parent) ->
        scope.parent = Parent.get school_id: stateParams.kindergarten, phone: parent.phone, ->
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_adult.html'

      scope.editChild = (child) ->
        scope.nickFollowing = true
        scope.child = Child.get school_id: stateParams.kindergarten, child_id: child.child_id, ->
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_child.html'

      scope.isPhoneDuplicated = (parent) ->
        return false if parent.phone is undefined
        undefined isnt _.find scope.parents, (p) ->
          (p.phone == parent.phone && p.parent_id != parent.parent_id)

      scope.isCardDuplicated = (card) ->
        return false if card is undefined || card.length < 10
        undefined isnt _.find scope.relationships, (r) ->
          r.card == card

      scope.uploadPic = (person, pic) ->
        scope.uploading = true
        Upload pic, (url) ->
          scope.$apply ->
            person.portrait = url if url isnt undefined
            scope.uploading = false
        , scope.adminUser.id

      handleError = (obj, res) ->
        Alert
          title: '保存' + obj + '失败'
          content: res.data.error_msg
          placement: "top-left"
          type: "danger"
          container: '.panel-body'
          duration: 3

      scope.saveParent = (parent) ->
        saveHook = parent.saveHook
        parent.$save ->
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()
          saveHook(parent) if typeof saveHook == 'function'
        , (res) ->
          handleError('家长', res)


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
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()
          scope.newParent (parent)->
            console.log 'invoking'
            scope.newRelationship child, parent

      scope.connectToExists = (child, parent) ->
        child.$save ->
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()
          scope.newRelationship child, parent

      scope.connectToChild = (parent) ->
        parent.$save ->
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

  ]

angular.module('kulebaoAdmin')
.controller 'connectedCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService',
    (scope, rootScope, stateParams, location, School, Class) ->
      scope.current_type = 'connected'

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
          location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

      scope.navigateTo = (s) ->
        location.path(location.path().replace(/\/type\/.+$/, '') + '/type/' + s.url) if stateParams.type != s.url
  ]

angular.module('kulebaoAdmin')
.controller 'ConnectedInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', 'classService', 'parentService', 'conversationService', 'relationshipService',
    (scope, rootScope, stateParams, location, School, Class, Parent, Chat, Relationship) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten
        scope.relationships = Relationship.bind(school_id: stateParams.kindergarten).query ->
          scope.loading = false

      scope.navigateTo = (c) ->
        location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

  ]

angular.module('kulebaoAdmin')
.controller 'ConnectedRelationshipCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', 'classService', 'parentService', 'conversationService', 'relationshipService',
    '$http',
    (scope, rootScope, stateParams, location, School, Class, Parent, Chat, Relationship, $http) ->
      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten
        scope.refreshRelationship()


      scope.refreshRelationship = ->
        scope.loading = true
        scope.relationships = Relationship.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id).query ->
          scope.loading = false

      generateCheckingInfo = (card, name, type) ->
        school_id: parseInt(stateParams.kindergarten)
        card_no: card
        card_type: 2
        notice_type: type
        record_url: 'http://suoqin-test.u.qiniudn.com/FoUJaV4r5L0bM0414mGWEIuCLEdL'
        parent: name
        timestamp: new Date().getTime()

      scope.sendMessage = (relationship, type) ->
        check = generateCheckingInfo(relationship.card, relationship.parent.name, type)
        $http({method: 'POST', url: '/kindergarten/' + stateParams.kindergarten + '/check', data: check}).success (data) ->
          alert 'error_code:' + data.error_code

      scope.delete = (card) ->
        Relationship.delete school_id: stateParams.kindergarten, card: card, ->
          scope.refreshRelationship()

      scope.$on 'refreshing', ->
        scope.refreshRelationship()
  ]
