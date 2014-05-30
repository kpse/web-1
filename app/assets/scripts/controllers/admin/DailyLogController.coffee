'use strict'

angular.module('kulebaoAdmin')
.controller 'AllDailyLogCtrl',
  [ '$scope', '$rootScope', '$stateParams', 'schoolService', 'classService', '$location', 'dailyLogService',
    'childService',
    (scope, rootScope, stateParams, School, Class, location, DailyLog, Child) ->
      rootScope.tabName = 'dailylog'

      scope.childrenInSchool = 0
      scope.refresh = ->
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: scope.kindergarten.school_id, ->
            _.forEach scope.kindergarten.classes, (clazz) ->
              clazz.dailyLog = DailyLog.query school_id: stateParams.kindergarten, class_id: clazz.class_id, ->
                scope.childrenInSchool = scope.childrenInSchool + clazz.dailyLog.length

            scope.allChildren = Child.query school_id: scope.kindergarten.school_id, connected: true, ->
              scope.heading = '全校应到 ' + scope.allChildren.length + '人 / 实到 ' + scope.childrenInSchool + ' 人'
            location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

      scope.$on 'update_charge', ->
        scope.refresh()

      scope.refresh()

      scope.navigateTo = (c) ->
        location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

  ]

angular.module('kulebaoAdmin')
.controller 'DailyLogInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', 'classService', 'parentService', '$modal', 'employeeService',
    '$alert', 'childService', 'dailyLogService',
    (scope, rootScope, stateParams, location, School, Class, Parent, Modal, Employee, Alert, Child, DailyLog) ->
      scope.loading = true
      scope.current_class = parseInt(stateParams.class_id)

      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
            scope.refresh()

      scope.currentClass = ->
        _.find scope.kindergarten.classes, (c) ->
          c.class_id == scope.current_class

      mappingDetails = (children, logs) ->
        _.forEach children, (c) ->
          c.checkInStatus = '未到校'
          checkedIn = _.find logs, (l) ->
            l.child_id == c.child_id
          if(checkedIn)
            switch checkedIn.notice_type
              when 1 then c.checkInStatus = '在校'
              when 0 then c.checkInStatus = '已离校'

      scope.refresh = ->
        scope.loading = true
        scope.children = Child.query school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true, ->
          scope.allLog = DailyLog.query school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
            mappingDetails scope.children, scope.allLog
            scope.childrenIn = _.reject scope.children, (c) ->
              c.checkInStatus == '未到校'
            scope.childrenNotIn = _.filter scope.children, (c) ->
              c.checkInStatus == '未到校'
            scope.loading = false

      scope.detail = (child)->
        location.path(location.path().replace(/\/list$/, '') + '/child/' + child.child_id)
  ]

angular.module('kulebaoAdmin')
.controller 'ChildDailyLogCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'childService', 'singleDailyLogService', 'imageCompressService',
    (scope, rootScope, stateParams, location, Child, DailyLog, Compressor) ->
      scope.child = Child.get school_id: stateParams.kindergarten, child_id: stateParams.child_id
      scope.allLogs = DailyLog.query school_id: stateParams.kindergarten, child_id: stateParams.child_id

      scope.compress = (url, width, height) ->
        Compressor.compress(url, width, height)

  ]