'use strict'

angular.module('kulebaoAdmin')
.controller 'unconnectedParentCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', 'parentService', '$q',
    (scope, rootScope, stateParams, $state, Parent, $q) ->
      scope.current_type = 'unconnectedParent'

      scope.$on 'refreshing', ->
        scope.refreshParents()

      scope.refreshParents = ->
        rootScope.loading = true
        scope.parents = Parent.query school_id: stateParams.kindergarten, connected: false, ->
          rootScope.loading = false

      scope.refreshParents()

      scope.delete = (parent) ->
        Parent.delete school_id: stateParams.kindergarten, phone: parent.phone, ->
          scope.refreshParents()

      scope.checkAll = (check) ->
        scope.parents = _.map scope.parents, (r) -> r.checked = check;r

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

      scope.navigateTo = (s) ->
        if stateParams.type != s.url
          rootScope.loading = true
          $state.go 'kindergarten.relationship.type', {kindergarten: stateParams.kindergarten, type: s.url}
  ]
