'use strict'

angular.module('kulebaoAdmin')
.controller 'AssignmentListCtrl',
  [ '$scope', '$rootScope', '$stateParams', 'schoolService', 'classService', '$location',
    (scope, rootScope, stateParams, School, Class, location) ->
      rootScope.tabName = 'assignment'
      scope.heading = '孩子在家里还需要学习什么？在这里告诉家长吧'

      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
          location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

      scope.navigateTo = (c) ->
        location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

  ]

angular.module('kulebaoAdmin')
.controller 'AssignmentsInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    (scope, rootScope, stateParams) ->
      scope.loading = true
      scope.current_class = parseInt(stateParams.class_id)
  ]
angular.module('kulebaoAdmin')
.controller 'AssignmentsCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', 'classService', 'assignmentService', '$modal', 'employeeService', 'uploadService',
    (scope, rootScope, stateParams, location, School, Class, Assignment, Modal, Employee, Upload) ->

      scope.newAssignment = ->
        new Assignment
          class_id: parseInt stateParams.class_id
          school_id: parseInt stateParams.kindergarten
          publisher: scope.adminUser.name

      scope.create = ->
        scope.assignment = scope.newAssignment()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/assignment.html'

      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
            scope.refresh()

      scope.refresh = ->
        scope.loading = true
        scope.assignments = Assignment.query school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
          scope.loading = false

      scope.edit = (assignment) ->
        scope.assignment = angular.copy(assignment)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/assignment.html'

      scope.save = (assignment) ->
        assignment.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.uploadPic = (assignment, pic) ->
        scope.uploading = true
        Upload pic, (url) ->
          scope.$apply ->
            assignment.icon_url = url if url isnt undefined
            scope.uploading = false
        , scope.adminUser.id

  ]