'use strict'

angular.module('kulebaoAdmin')
.controller 'AssignmentListCtrl',
    [ '$scope', '$rootScope', '$stateParams', 'schoolService', 'classService', '$location',
      'assignmentService', '$modal',
      (scope, rootScope, stateParams, School, Class, location, Assignment, Modal) ->
        rootScope.tabName = 'assignment'
        scope.heading = '按班级布置作业'

        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
            location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

        scope.navigateTo = (c) ->
          location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

    ]

angular.module('kulebaoAdmin')
.controller 'AssignmentsInClassCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', 'classService', 'assignmentService', '$modal',
      (scope, rootScope, stateParams, location, School, Class, Assignment, Modal) ->
        scope.loading = true
        scope.current_class = parseInt(stateParams.class_id)

        scope.adminUser =
          id: 1
          name: '学校某老师'

        scope.create = ->
          rootScope.editingAssignment = new Assignment
            class_id: parseInt stateParams.class_id
            school_id: parseInt stateParams.kindergarten
            publisher: scope.adminUser.name
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/assignment.html'


        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.bind(school_id: scope.kindergarten.school_id).query ->
            scope.refresh()
            scope.loading = false

        scope.refresh = ->
          scope.assignments = Assignment.bind(school_id: scope.kindergarten.school_id, class_id: stateParams.class_id).query ->
            _.forEach scope.assignments, (a) ->
              a.class_name = (_.find scope.kindergarten.classes, (c) ->
                c.class_id == a.class_id).name

        scope.goDetail = (assignment) ->
          rootScope.editingAssignment = angular.copy(assignment)
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/assignment.html'

        scope.$on 'refreshAssignments', ->
          scope.refresh()

    ]

angular.module('kulebaoAdmin')
.controller 'AssignmentCtrl',
    [ '$scope', '$rootScope', '$http', 'uploadService',
      (scope, rootScope, $http, uploadService) ->
        if rootScope.editingAssignment isnt undefined
          scope.assignment = rootScope.editingAssignment
        else
          scope.$hide()

        scope.save = (assignment) ->
          assignment.$save ->
            scope.$hide()
            scope.$emit 'refreshAssignments'

        upload = (file, callback)->
          return callback(undefined) if file is undefined
          $http.get('/ws/fileToken?bucket=suoqin-test').success (data)->
            uploadService.send file, data.token, (remoteFile) ->
              callback(remoteFile.url)

        scope.uploadPic = (assignment, pic) ->
          upload pic, (url) ->
            scope.$apply ->
              assignment.icon_url = url if url isnt undefined

    ]