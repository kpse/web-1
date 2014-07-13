angular.module('kulebaoOp').controller 'OpReportingCtrl',
  ['$scope', '$rootScope', '$location', '$http', 'parentService', 'childService', 'employeeService',
   'schoolEmployeesService', 'classService', 'schoolService', 'activeCountService', 'chargeService',
    (scope, rootScope, location, $http, Parent, Child, CurrentUser, Employee, Class, School, ActiveCount, Charge) ->
      rootScope.tabName = 'reporting'
      scope.adminUser = CurrentUser.get()

      scope.loading = true
      scope.allClasses = []
      scope.allEmployees = []
      scope.allChildren = []
      scope.allParents = []
      scope.allActiveMembers = 0
      scope.allAuthorised = 0
      scope.kindergartens = School.query ->
        _.forEach scope.kindergartens, (k) ->
          k.classes = Class.query school_id: k.school_id, ->
            scope.allClasses = scope.allClasses.concat k.classes
          k.parents = Parent.query school_id: k.school_id, ->
            scope.allParents = scope.allParents.concat k.parents
          k.children = Child.query school_id: k.school_id, ->
            scope.allChildren = scope.allChildren.concat k.children
          k.employees = Employee.query school_id: k.school_id, ->
            scope.allEmployees = scope.allEmployees.concat k.employees
          k.active = ActiveCount.get school_id: k.school_id, ->
            scope.allActiveMembers = scope.allActiveMembers + k.active.activated
          k.charge = Charge.query school_id: k.school_id, ->
            scope.allAuthorised = scope.allAuthorised + k.charge[0].total_phone_number
        scope.loading = false

      scope.detail = (kg) ->
        location.path('main/school_report/' + kg.school_id)

  ]

.controller 'OpSchoolReportingCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http', 'parentService', 'childService', 'employeeService',
   'schoolEmployeesService', 'classService', 'schoolService', 'adminNewsPreview', 'sessionStatsService', 'historyStatsService',
   'dailyLogService', 'activeCountService',
    (scope, rootScope, stateParams, $http, Parent, Child, CurrentUser, Employee, Class, School, News, Chat, History, DailyLog, ActiveCount) ->
      scope.adminUser = CurrentUser.get()

      scope.loading = true

      scope.childrenInSchool = 0
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, ->
          _.forEach scope.kindergarten.classes, (clazz) ->
            clazz.dailyLogs = DailyLog.query school_id: stateParams.school_id, class_id: clazz.class_id, ->
              scope.childrenInSchool = scope.childrenInSchool + clazz.dailyLogs.length

          scope.parents = Parent.query school_id: stateParams.school_id
          scope.children = Child.query school_id: stateParams.school_id, connected: true
          scope.employees = Employee.query school_id: stateParams.school_id
          scope.allNews = News.query school_id: stateParams.school_id
          scope.allChats = Chat.get school_id: stateParams.school_id
          scope.allHistoryRecords = History.get school_id: stateParams.school_id
          scope.active = ActiveCount.get school_id: stateParams.school_id
          scope.loading = false

  ]




