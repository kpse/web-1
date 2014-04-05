'use strict'


angular.module('kulebaoAdmin')
.controller 'unconnectedChildCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService', 'parentService',
   'relationshipService', '$modal', 'childService'
    (scope, rootScope, stateParams, location, School, Class, Parent, Relationship, Modal, Child) ->
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
