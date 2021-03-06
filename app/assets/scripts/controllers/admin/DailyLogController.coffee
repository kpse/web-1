'use strict'

angular.module('kulebaoAdmin')
.controller 'AllDailyLogCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$location', 'dailyLogService',
    'childService', 'accessClassService', 'imageCompressService',
    (scope, rootScope, stateParams, location, DailyLog, Child, AccessClass, Compressor) ->
      rootScope.tabName = 'dailylog'

      scope.refresh = ->
        scope.childrenInSchool = 0
        _.each scope.kindergarten.classes, (clazz) ->
          clazz.dailyLog = DailyLog.query school_id: stateParams.kindergarten, class_id: clazz.class_id, ->
            scope.childrenInSchool = scope.childrenInSchool + clazz.dailyLog.length

        scope.allChildren = Child.query school_id: scope.kindergarten.school_id, connected: true, ->
          scope.heading = '全校应到 ' + scope.allChildren.length + '人 / 实到 ' + scope.childrenInSchool + ' 人'
        AccessClass(scope.kindergarten.classes)

      scope.$on 'update_charge', ->
        scope.refresh()

      scope.refresh()

      scope.navigateTo = (c) ->
        location.path("kindergarten/#{stateParams.kindergarten}/dailylog/class/#{c.class_id}/list")

      scope.compress = (url, width, height) ->
        return '' unless url?
        Compressor.compress(url, width, height)
      scope.$on 'clear_search', ->
        scope.searchText = ''
  ]

.controller 'DailyLogInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'parentService', '$modal',
    'childService', 'dailyLogService', 'statisticsDailyLogService',
    (scope, rootScope, stateParams, location, Parent, Modal, Child, DailyLog, Statistics) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.refresh()

      scope.currentClass = ->
        _.find scope.kindergarten.classes, (c) ->
          c.class_id == scope.current_class

      mappingDetails = (children, logs) ->
        _.each children, (c) ->
          c.checkInStatus = '未到校'
          checkedIn = _.find logs, (l) ->
            l.child_id == c.child_id
          if(checkedIn)
            switch checkedIn.notice_type
              when 1 then c.checkInStatus = '在校'
              when 0 then c.checkInStatus = '已离校'

      scope.refresh = ->
        rootScope.loading = true
        scope.children = Child.query school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true, ->
          scope.allLog = DailyLog.query school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
            mappingDetails scope.children, scope.allLog
            scope.childrenIn = _.reject scope.children, (c) ->
              c.checkInStatus == '未到校'
            scope.childrenNotIn = _.filter scope.children, (c) ->
              c.checkInStatus == '未到校'
            rootScope.loading = false

      scope.detail = (child)->
        scope.$emit 'clear_search'
        location.path "kindergarten/#{stateParams.kindergarten}/dailylog/class/#{child.class_id}/child/#{child.child_id}"

      scope.d3Data = []
      scope.allData = Statistics.query school_id: stateParams.kindergarten, ->
        scope.d3Data = _.filter scope.allData, (d) ->
          d.class_id == scope.current_class

      scope.title = 'DemoTitle'

  ]
.controller 'ChildDailyLogCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    'childService', 'singleDailyLogService', 'relationshipService',
    'classManagerService',
    (scope, rootScope, stateParams, Child, DailyLog, Relationship, Manager) ->
      scope.child = Child.get school_id: stateParams.kindergarten, child_id: stateParams.child_id, ->
        scope.relationships = Relationship.query school_id: stateParams.kindergarten, child: scope.child.child_id
        scope.managers = Manager.query school_id: stateParams.kindergarten, class_id: stateParams.class_id
        scope.heading = "#{scope.child.name}小朋友的到校和离校记录"

      scope.allLogs = DailyLog.query school_id: stateParams.kindergarten, child_id: stateParams.child_id, ->
          rootScope.loading = false
        , (err) ->
          rootScope.loading = false

      scope.displayType = (type) ->
        switch type
          when 1 then '到校'
          when 0 then '离校'
          when 10 then '早上上车'
          when 11 then '早上下车'
          when 12 then '下午上车'
          when 13 then '下午下车'

  ]