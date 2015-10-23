'use strict'

angular.module('kulebaoAdmin')
.controller 'AssessListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'accessClassService',
    (scope, rootScope, stateParams, location, AccessClass) ->
      rootScope.tabName = 'baby-status'
      scope.heading = '评价宝宝最近的表现'

      AccessClass(scope.kindergarten.classes)

      scope.navigateTo = (c) ->
        rootScope.loading = true
        location.path("kindergarten/#{stateParams.kindergarten}/baby-status/class/#{c.class_id}/list")

  ]
.controller 'AssessInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'assessService', 'childService',
    (scope, rootScope, stateParams, location, Assess, Child) ->
      rootScope.loading = true
      scope.current_class = parseInt(stateParams.class_id)

      scope.children = Child.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true).query ->
        _.forEach scope.children, (c) ->
          c.status = Assess.bind(school_id: stateParams.kindergarten, child_id: c.child_id, most: 1).query ->
            c.recentStatus = c.status[0]
        rootScope.loading = false

      scope.goDetail = (child) ->
        rootScope.loading = true
        location.path "kindergarten/#{stateParams.kindergarten}/baby-status/class/#{child.class_id}/child/#{child.child_id}"

  ]
.controller 'AssessCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', '$http', 'assessService', 'childService', '$modal',
    (scope, rootScope, stateParams, location, $http, Assess, Child, Modal) ->

      rootScope.loading = true
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
  ]