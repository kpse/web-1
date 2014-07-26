'use strict'


angular.module('kulebaoAdmin')
.controller 'unconnectedChildCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'childService',
    (scope, rootScope, stateParams, location, Child) ->
      scope.current_type = 'unconnectedChild'

      scope.$on 'refreshing', ->
        scope.refreshChildren()

      scope.refreshChildren = ->
        scope.loading = true
        scope.children = Child.query school_id: stateParams.kindergarten, connected: false, ->
          scope.loading = false

      scope.refreshChildren()

      scope.delete = (child) ->
        Child.delete school_id: stateParams.kindergarten, child_id: child.child_id, ->
          scope.refreshChildren()
  ]
