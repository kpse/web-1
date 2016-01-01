angular.module('kulebaoOp').controller 'OpReportingCtrl',
  ['$scope', '$rootScope', '$q', '$location', '$http',
   'schoolEmployeesService', 'classService', 'schoolService', 'activeCountService', 'chargeService', 'StatsServiceV4',
   'monthlyChildRateService', 'monthlySchoolRateService',
    (scope, rootScope, $q, location, $http, Employee, Class, School, ActiveCount, Charge, Statistics,
     ChildRate, SchoolRate) ->
      rootScope.tabName = 'reporting'

      Monthly = Statistics 'monthly'
      Daily = Statistics 'daily'

      scope.refresh = (TimeEndPoint = Monthly)->
        rootScope.loading = true
        scope.allChildren = 0
        scope.allParents = 0
        scope.allLoggedOnce = 0
        scope.allLoggedEver = 0
        queue = [School.query().$promise, TimeEndPoint.query().$promise]
        $q.all(queue).then (q) ->
          scope.kindergartens = q[0]
          allStatsData = _.groupBy q[1], 'school_id'
          scope.kindergartens = _.map scope.kindergartens, (k) ->
            k.monthly = _.first allStatsData[k.school_id]
            k.monthlyRate = SchoolRate(k.monthly)
            k.monthlyChildRate = ChildRate(k.monthly)
            k.parents = k.monthly.parent_count
            k.children = k.monthly.child_count
            k
          scope.allLoggedOnce = _.sum scope.kindergartens, 'monthly.logged_once'
          scope.allLoggedEver = _.sum scope.kindergartens, 'monthly.logged_ever'
          scope.allParents  = _.sum scope.kindergartens, 'parents'
          scope.allChildren  = _.sum scope.kindergartens, 'children'
          rootScope.loading = false

      scope.refresh()

      scope.switchToRealTime = ->
        scope.refresh(Daily)
        scope.currentMonth = '实时'
        scope.csvName = "real_time_report_#{yesterday(new Date())}.csv"

      scope.detail = (kg) ->
        location.path "main/school_report/#{kg.school_id}"

      lastMonthOfNow = (time) ->
        time.setDate(1)
        time.setMonth(time.getMonth() - 1)
        time.getFullYear() + ('0' + (time.getMonth() + 1)).slice(-2) + ''
      yesterday = (time) ->
        time.setDate(time.getDate() - 1)
        time.getFullYear() + ('0' + (time.getMonth() + 1)).slice(-2) + '' + time.getDate()
      scope.currentMonth = lastMonthOfNow(new Date())

      scope.forceToReCalculate = ->
      scope.export = ->
        _.map scope.kindergartens, (k) ->
          id: k.school_id
          name: k.full_name
          children: k.monthly.child_count
          parents: k.monthly.parent_count
          user: k.monthly.logged_ever
          active: k.monthly.logged_once
          rate: k.monthlyRate
          childRate: k.monthlyChildRate

      scope.exportHeader = -> ['学校ID', '学校全称', '学生总数', '家长总数', '总用户数', '当月用户数', '当月激活率', '当月活跃度']
      scope.csvName = "monthly_report_#{scope.currentMonth}.csv"

  ]

.controller 'OpSchoolReportingCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http',
   'schoolEmployeesService', 'classService', 'schoolService', 'adminNewsPreview', 'StatsService', 'activeCountService',
   'monthlyChildRateService', 'monthlySchoolRateService', 'StatsServiceV4',
    (scope, rootScope, stateParams, $http, Employee, Class, School, News, Statistics, ActiveCount,
     ChildRate, SchoolRate, StatisticsV4) ->
      rootScope.tabName = 'reporting'
      rootScope.loading = true

      Chat = Statistics 'session'
      History = Statistics 'history'
      DailyLog = Statistics 'dailylog'
      Monthly = StatisticsV4 'monthly'

      scope.childrenInSchool = 0
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, ->
          _.each scope.kindergarten.classes, (clazz) ->
            clazz.dailyLogs = DailyLog.query school_id: stateParams.school_id, class_id: clazz.class_id, ->
              scope.childrenInSchool = scope.childrenInSchool + clazz.dailyLogs.length

          scope.employees = Employee.query school_id: stateParams.school_id
          scope.allNews = News.query school_id: stateParams.school_id, publisher_id: scope.adminUser.id
          scope.allChats = Chat.get school_id: stateParams.school_id
          scope.allHistoryRecords = History.get school_id: stateParams.school_id
          scope.active = ActiveCount.get school_id: stateParams.school_id
          scope.monthly = Monthly.get school_id: stateParams.school_id, ->
            scope.parents = scope.monthly.parent_count
            scope.children = scope.monthly.child_count
            scope.monthlyRate = SchoolRate(scope.monthly)
            scope.monthlyChildRate = ChildRate(scope.monthly)
          rootScope.loading = false

  ]




