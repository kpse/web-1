'use strict'

angular.module('kulebaoAdmin')
.controller 'unconnectedParentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'parentService',
    (scope, rootScope, stateParams, location, Parent) ->
      scope.current_type = 'unconnectedParent'

      scope.$on 'refreshing', ->
        scope.refreshParents()

      scope.refreshParents = ->
        scope.loading = true
        scope.parents = Parent.query school_id: stateParams.kindergarten, connected: false, ->
          scope.loading = false

      scope.refreshParents()

      scope.delete = (parent) ->
        Parent.delete school_id: stateParams.kindergarten, phone: parent.phone, ->
          scope.refreshParents()
  ]
