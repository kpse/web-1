'use strict'


angular.module('kulebaoAdmin')
.controller 'unconnectedChildCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', '$q', 'childService',
    (scope, rootScope, stateParams, location, $q, Child) ->
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

      scope.checkAll = (check) ->
        _.forEach scope.children, (r) -> r.checked = check

      scope.multipleDelete = ->
        checked = _.filter scope.children, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (c) -> Child.delete(school_id: stateParams.kindergarten, child_id: c.child_id).$promise
        all = $q.all queue
        all.then (q) ->
          scope.refreshChildren()

      scope.hasSelection = (children) ->
        _.some children, (r) -> r.checked? && r.checked == true

      scope.singleSelection = (child) ->
        allChecked = _.every scope.children, (r) -> r.checked? && r.checked == true
        scope.selection.allCheck = allChecked

      scope.selection = allCheck: false
  ]
