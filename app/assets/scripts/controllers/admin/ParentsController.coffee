'use strict'

angular.module('kulebaoAdmin')
.controller 'unconnectedParentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'parentService', '$q',
    (scope, rootScope, stateParams, location, Parent, $q) ->
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

      scope.checkAll = (check) ->
        _.forEach scope.parents, (r) -> r.checked = check

      scope.multipleDelete = ->
        checked = _.filter scope.parents, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (p) -> Parent.delete(school_id: stateParams.kindergarten, phone: p.phone).$promise
        all = $q.all queue
        all.then (q) ->
          scope.refreshParents()

      scope.hasSelection = (parents) ->
        _.some parents, (r) -> r.checked? && r.checked == true

      scope.singleSelection = (relationship) ->
        allChecked = _.every scope.parents, (r) -> r.checked? && r.checked == true
        scope.selection.allCheck = allChecked

      scope.selection = allCheck: false
  ]
