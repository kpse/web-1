'use strict'

angular.module('kulebaoAdmin')
.controller 'ScheduleCtrl', [ '$scope', '$rootScope', '$stateParams',
                              '$location', 'schoolService', '$http', 'scheduleService', '$timeout', 'classService',
  (scope, rootScope, stateParams, location, School, $http, Schedule, $timeout, Class) ->
    rootScope.tabName = 'schedule'
    scope.heading = '课程表'
    scope.current_class = parseInt stateParams.class_id


    scope.loading = true
    scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
      scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
        location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

    scope.navigateTo = (c) ->
      location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')
]

angular.module('kulebaoAdmin')
.controller 'ClassScheduleCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', '$http', 'scheduleService', '$alert'
    (scope, rootScope, stateParams, location, School, $http, Schedule, Alert) ->
      scope.schedule_changed = false
      scope.isEditing = false

      scope.schedules = Schedule.query school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
        if scope.schedules[0] isnt undefined
          scope.schedule = scope.schedules[0]
        else
          scope.schedule = new Schedule
            school_id: parseInt stateParams.kindergarten
            class_id: parseInt stateParams.class_id

        scope.$watch 'schedule', (oldv, newv) ->
          scope.schedule_changed = true if (newv isnt oldv)
        , true

      scope.toggleEditing = (e) ->
        e.stopPropagation()
        scope.isEditing = !scope.isEditing
        scope.schedule_changed = false if scope.isEditing
        console.log 'scope.schedule changed: ' + scope.schedule_changed
        if scope.schedule_changed
          scope.schedule.$save ->
            scope.schedule_changed = false
            Alert
              content: '课程保存成功'
              container: '.parents-class-view'
              type: 'success'
  ]