'use strict'

angular.module('kulebaoAdmin')
.controller 'AssessListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'accessClassService',
    (scope, rootScope, stateParams, location, AccessClass) ->
      rootScope.tabName = 'baby-status'
      scope.heading = '评价宝宝最近的表现'

      AccessClass(scope.kindergarten.classes)

      scope.$on 'clearSearch', ->
        scope.searchText = ''

  ]
.controller 'AssessInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$timeout',
    '$state', 'assessService', 'childService',
    (scope, rootScope, stateParams, $timeout, $state, Assess, Child) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.children = Child.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true).query ->
        _.forEach scope.children, (c) ->
          c.status = Assess.bind(school_id: stateParams.kindergarten, child_id: c.child_id, most: 1).query ->
            c.recentStatus = c.status[0]
        rootScope.loading = false

      scope.goDetail = (child) ->
        scope.$emit 'clearSearch'
        rootScope.loading = true
        $state.go 'kindergarten.assess.class.child', {kindergarten: stateParams.kindergarten, class_id: child.class_id, child: child.child_id}

      scope.navigateTo = (c) ->
        if c.class_id != scope.current_class || !$state.is 'kindergarten.assess.class.list'
          rootScope.loading = true
          $timeout ->
            $state.go 'kindergarten.assess.class.list', {kindergarten: stateParams.kindergarten, class_id: c.class_id}

      rootScope.loading = false
  ]
.controller 'AssessCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', '$http', 'assessService', 'childService', '$modal',
    (scope, rootScope, stateParams, location, $http, Assess, Child, Modal) ->
      scope.child = Child.bind(school_id: stateParams.kindergarten, child_id: stateParams.child).get ->
        scope.refreshAssess()

      scope.refreshAssess = ->
        rootScope.loading = true
        scope.allAssess = Assess.bind(school_id: stateParams.kindergarten, child_id: stateParams.child).query ->
          rootScope.loading = false

      scope.createAssess = ->
        new Assess
          child_id: stateParams.child
          school_id: parseInt stateParams.kindergarten
          publisher: scope.adminUser.name
          publisher_id: scope.adminUser.id
          emotion: 3
          activity: 3
          rest: 3
          game: 3
          self_care: 3
          exercise: 3
          manner: 3
          dining: 3
          comments: ''

      scope.create = ->
        scope.newAssess = scope.createAssess()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_assess.html'

      scope.preview = (assess) ->
        scope.assess = assess
        scope.currentModal = Modal
          contentTemplate: 'templates/admin/view_assess.html'
          scope: scope

      scope.save = (assess) ->
        rootScope.loading = true
        assess.$save ->
          scope.refreshAssess()
          scope.currentModal.hide()

      scope.remove = (assess) ->
        rootScope.loading = true
        assess.$delete ->
          scope.refreshAssess()
          scope.currentModal.hide()

      rootScope.loading = false
  ]