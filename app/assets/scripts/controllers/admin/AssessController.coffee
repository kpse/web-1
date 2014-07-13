'use strict'

angular.module('kulebaoAdmin')
.controller 'AssessListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    'schoolService', 'classService', '$location', 'accessClassService',
    (scope, rootScope, stateParams, School, Class, location, AccessClass) ->
      rootScope.tabName = 'baby-status'
      scope.heading = '评价宝宝最近的表现'

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
          AccessClass(scope.kindergarten.classes)

      scope.navigateTo = (c) ->
        location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

  ]
.controller 'AssessInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', 'classService', 'assessService', 'childService',
    (scope, rootScope, stateParams, location, School, Class, Assess, Child) ->
      scope.loading = true
      scope.current_class = parseInt(stateParams.class_id)

      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query()
        scope.children = Child.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true).query ->
          _.forEach scope.children, (c) ->
            c.status = Assess.bind(school_id: stateParams.kindergarten, child_id: c.child_id, most: 1).query ->
              c.recentStatus = c.status[0]
          scope.loading = false

      scope.goDetail = (child) ->
        if (location.path().indexOf('/list') > 0 )
          location.path location.path().replace(/\/list$/, '/child/' + child.child_id)
        else
          location.path location.path().replace(/\/child\/\d+$/, '') + '/child/' + child.child_id


  ]
.controller 'AssessCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', '$http', 'classService', 'assessService', 'childService', '$modal',
    '$popover', '$tooltip', 'employeeService'
    (scope, rootScope, stateParams, location, School, $http, Class, Assess, Child, Modal, Popover, Tooltip, Employee) ->
      scope.adminUser = Employee.get()

      scope.loading = true
      scope.child = Child.bind(school_id: stateParams.kindergarten, child_id: stateParams.child).get ->
        scope.refreshAssess()

      scope.refreshAssess = ->
        scope.loading = true
        scope.allAssess = Assess.bind(school_id: stateParams.kindergarten, child_id: stateParams.child).query ->
          scope.loading = false

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
        assess.$save ->
          scope.refreshAssess()
          scope.currentModal.hide()

      scope.remove = (assess) ->
        assess.$delete ->
          scope.refreshAssess()
          scope.currentModal.hide()
  ]