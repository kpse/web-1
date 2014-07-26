'use strict'

angular.module('kulebaoAdmin')
.controller 'AssignmentListCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$location', 'accessClassService',
    (scope, rootScope, stateParams, location, AccessClass) ->
      rootScope.tabName = 'assignment'
      scope.heading = '孩子在家里还需要学习什么？在这里告诉家长吧'

      AccessClass(scope.kindergarten.classes)

      scope.navigateTo = (c) ->
        location.path("kindergarten/#{stateParams.kindergarten}/assignment/class/#{c.class_id}/list")

  ]
.controller 'AssignmentsInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    (scope, rootScope, stateParams) ->
      scope.loading = true
      scope.current_class = parseInt(stateParams.class_id)
  ]
.controller 'AssignmentsCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'assignmentService', '$modal',
    (scope, rootScope, stateParams, location, Assignment, Modal) ->
      scope.newAssignment = ->
        new Assignment
          class_id: parseInt stateParams.class_id
          school_id: parseInt stateParams.kindergarten
          publisher: scope.adminUser.name
          publisher_id: scope.adminUser.id

      scope.create = ->
        scope.assignment = scope.newAssignment()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/assignment.html'

      scope.buttonLabel = '上传图片'

      scope.refresh = ->
        scope.loading = true
        scope.assignments = Assignment.query school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
          scope.loading = false

      scope.refresh()

      scope.edit = (assignment) ->
        scope.assignment = angular.copy(assignment)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/assignment.html'

      scope.save = (assignment) ->
        assignment.$save ->
          scope.refresh()
          scope.currentModal.hide()
  ]