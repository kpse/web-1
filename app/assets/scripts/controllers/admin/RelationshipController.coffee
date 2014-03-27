'use strict'

angular.module('kulebaoAdmin')
.controller 'RelationshipMainCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService', 'parentService',
   'relationshipService', '$modal', 'childService', '$http', '$alert', 'uploadService', '$timeout'
    (scope, rootScope, stateParams, location, School, Class, Parent, Relationship, Modal, Child, $http, Alert, Upload, $timeout) ->
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
        }
      ]

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten

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

      scope.createRelationship = (child, parent)->
        new Relationship
          school_id: parseInt stateParams.kindergarten
          relationship: '妈妈'
          child: child
          parent: parent

      scope.newParent = (saveHook)->
        scope.parents = Parent.query school_id: stateParams.kindergarten, ->
          scope.parent = scope.createParent()
          scope.parent.saveHook = saveHook
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_adult.html'

      scope.newChild = ->
        scope.child = scope.createChild()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_child.html'

      scope.newRelationship = (child, parent)->
        scope.relationship = scope.createRelationship(child, parent)
        scope.parents = Parent.query school_id: stateParams.kindergarten, ->
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
        Upload pic, (url) ->
          scope.$apply ->
            person.portrait = url if url isnt undefined


      scope.saveParent = (parent) ->
        saveHook = parent.saveHook
        parent.$save ->
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()
          saveHook(parent) if saveHook?()
        , (res)->
          Alert
            title: '创建失败'
            content: res.data.error_msg
            placement: "top-left"
            type: "danger"
            show: true
            container: '.panel-body'
            duration: 3

      scope.saveRelationship = (relationship) ->
        relationship.$save ->
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()

      scope.saveChild = (child) ->
        child.$save ->
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()

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
            scope.connectToExists child, parent

      scope.connectToExists = (child, parent) ->
        child.$save ->
          scope.$broadcast 'refreshing'
          scope.currentModal.hide()
          scope.newRelationship child, parent

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
.controller 'unconnectedParentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService', 'parentService',
    (scope, rootScope, stateParams, location, School, Class, Parent) ->
      scope.current_type = 'unconnectedParent'

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.refreshParents()

      scope.$on 'refreshing', ->
        scope.refreshParents()

      scope.refreshParents = ->
        scope.loading = true
        scope.parents = Parent.query school_id: stateParams.kindergarten, connected: false, ->
          scope.loading = false

      scope.navigateTo = (s) ->
        location.path(location.path().replace(/\/type\/.+$/, '') + '/type/' + s.url) if stateParams.type != s.url

      scope.delete = (parent) ->
        Parent.delete school_id: stateParams.kindergarten, phone: parent.phone, ->
          scope.refreshParents()
  ]

angular.module('kulebaoAdmin')
.controller 'unconnectedChildCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService', 'parentService',
   'relationshipService', '$modal', 'childService', '$http', '$alert', 'uploadService',
    (scope, rootScope, stateParams, location, School, Class, Parent, Relationship, Modal, Child, $http, Alert, Upload) ->
      scope.current_type = 'unconnectedChild'

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.refreshChildren()

      scope.$on 'refreshing', ->
        scope.refreshChildren()

      scope.refreshChildren = ->
        scope.loading = true
        scope.children = Child.query school_id: stateParams.kindergarten, connected: false, ->
          scope.loading = false

      scope.navigateTo = (s) ->
        location.path(location.path().replace(/\/type\/.+$/, '') + '/type/' + s.url) if stateParams.type != s.url

      scope.delete = (child) ->
        Child.delete school_id: stateParams.kindergarten, child_id: child.child_id, ->
          scope.refreshChildren()
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
    (scope, rootScope, stateParams, location, School, Class, Parent, Chat, Relationship) ->
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
