'use strict'

angular.module('kulebaoAdmin')
.controller 'RelationshipCtrl',
    ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService', 'parentService',
     'relationshipService', '$modal', 'childService', '$http'
      (scope, rootScope, stateParams, location, School, Class, Parent, Relationship, Modal, Child, $http) ->
        rootScope.tabName = 'relationship'
        scope.loading = true
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten

          scope.refreshRelationship()


        scope.refreshRelationship = ->
          scope.loading = true
          scope.relationships = Relationship.bind(school_id: stateParams.kindergarten).query ->
            scope.parents = Parent.query school_id: stateParams.kindergarten
            scope.children = Child.query school_id: stateParams.kindergarten
            scope.loading = false

        scope.createParent = ->
          new Parent
            school_id: parseInt stateParams.kindergarten
            birthday: '1980-1-1'
            gender: 1
            name: '马大帅'
            kindergarten: scope.kindergarten.school_info

        scope.createChild = ->
          new Child
            name: '宝宝名字'
            nick: '宝宝小名'
            birthday: '2009-1-1'
            gender: 1
            class_id: scope.kindergarten.classes[0].class_id
            school_id: parseInt stateParams.kindergarten

        scope.createRelationship = ->
          new Relationship
            school_id: parseInt stateParams.kindergarten
            relationship: '妈妈'

        scope.newParent = ->
          scope.parents = Parent.query school_id: stateParams.kindergarten, ->
            scope.parent = scope.createParent()
            scope.currentModal = Modal
              scope: scope
              contentTemplate: 'templates/admin/add_adult.html'

        scope.newChild = ->
          scope.child = scope.createChild()
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_child.html'

        scope.newRelationship = ->
          scope.relationship = scope.createRelationship()
          scope.parents = Parent.query school_id: stateParams.kindergarten, ->
            scope.children = Child.query school_id: stateParams.kindergarten, ->
              scope.currentModal = Modal
                scope: scope
                contentTemplate: 'templates/admin/add_connection.html'

        scope.editParent = (parent) ->
          scope.parent = parent
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_adult.html'

        scope.editChild = (child) ->
          scope.child = child
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_child.html'


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
          parent.$save ->
            scope.refreshRelationship()
            scope.currentModal.hide()


        scope.saveRelationship = (relationship) ->
          relationship.$save ->
            scope.refreshRelationship()
            scope.currentModal.hide()

        scope.saveChild = (child) ->
          child.$save ->
            scope.refreshRelationship()
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
    ]

