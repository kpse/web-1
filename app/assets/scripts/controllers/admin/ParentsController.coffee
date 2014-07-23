'use strict'

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

      scope.delete = (parent) ->
        Parent.delete school_id: stateParams.kindergarten, phone: parent.phone, ->
          scope.refreshParents()
  ]
