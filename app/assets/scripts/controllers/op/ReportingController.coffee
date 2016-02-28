angular.module('kulebaoOp').controller 'OpReportingCtrl',
  ['$scope', '$rootScope', '$q', '$location', '$http', '$filter',
   'schoolEmployeesService', 'classService', 'schoolService', 'activeCountService', 'chargeService', 'StatsServiceV4',
    'agentLocationService', 'agentManagementService', 'agentSchoolService', 'fullResponseService', 'schoolStatsService',
    (scope, rootScope, $q, location, $http, $filter, Employee, Class, School, ActiveCount, Charge, Statistics, Location,
    Agent, AgentSchool, FullResponse, SchoolHistory) ->
      rootScope.tabName = 'reporting'

      Monthly = Statistics 'monthly'
      Daily = Statistics 'daily'

      scope.refresh = (TimeEndPoint = Monthly)->
        rootScope.loading = true
        scope.allChildren = 0
        scope.allParents = 0
        scope.allLoggedOnce = 0
        scope.allLoggedEver = 0
        queue = [School.query().$promise, TimeEndPoint.query().$promise, Agent.query().$promise]
        $q.all(queue).then (q) ->
          scope.kindergartens = q[0]
          allStatsData = _.groupBy q[1], 'school_id'
          scope.agents = q[2]
          _.each scope.agents, (a, i) ->
            FullResponse(AgentSchool, agent_id: a.id, most:25).then (d2) ->
              a.schoolIds = d2
              _.each scope.kindergartens, (k) ->
                k.agent = a if _.pluck(a.schoolIds, 'school_id').indexOf(k.school_id) >= 0


          scope.kindergartens = _.map scope.kindergartens, (k) ->
            k.monthly = _.first allStatsData[k.school_id]
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
        scope.currentWeek = '实时'
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
      scope.currentWeek = lastMonthOfNow(new Date())

      scope.forceToReCalculate = ->
      scope.export = ->
        _.map scope.kindergartens, (k) ->
          id: k.school_id
          created_at: $filter('date')(k.created_at, 'yyyy-MM-dd')
          name: k.full_name
          address: Location.provinceOf k.address
          address2: Location.cityOf k.address
          address3: Location.countyOf k.address
          agent: if k.agent? then k.agent.name else ''
          children: k.monthly.child_count
          parents: k.monthly.parent_count
          user: k.monthly.logged_ever
          active: k.monthly.logged_once

      scope.exportHeader = -> ['学校ID', '开园时间', '学校全称', '省', '市', '区(县)', '所属代理商', '学生总数', '家长总数', '总用户数', '当月用户数', '当月激活率', '当月活跃度']
      scope.csvName = "monthly_report_#{scope.currentWeek}.csv"

      scope.singleExport = (kg) ->
        p = SchoolHistory.query(school_id: kg.school_id).$promise
        $q (resolve, reject) ->
          p.then (allMonths) ->
            resolve(_.map allMonths, (m) ->
              id: kg.school_id
              name: kg.full_name
              address: Location.provinceOf kg.address
              address2: Location.cityOf kg.address
              address3: Location.countyOf kg.address
              agent: if kg.agent? then kg.agent.name else ''
              month: m.month
              children: m.child_count
              parents: m.parent_count
              user: m.logged_ever
              active: m.logged_once
            )


      scope.singleExportHeader = -> ['学校ID', '学校全称', '省', '市', '区(县)', '所属代理商', '月份', '学生总数', '家长总数', '总用户数', '当月用户数', '当月激活率', '当月活跃度']
      scope.singleExportCSVName = (kg) -> "#{kg.school_id}_#{kg.full_name}_历史数据汇总.csv"

  ]

.controller 'OpSchoolReportingCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http',
   'schoolEmployeesService', 'classService', 'schoolService', 'adminNewsPreview', 'StatsService', 'activeCountService',
   'monthlyActiveRateService', 'totalActiveRateService', 'StatsServiceV4',
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
          rootScope.loading = false

  ]




