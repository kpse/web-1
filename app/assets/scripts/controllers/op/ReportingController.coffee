angular.module('kulebaoOp').controller 'OpReportingCtrl',
  ['$scope', '$rootScope', '$location', '$http', 'parentService', 'childService',
   'schoolEmployeesService', 'classService', 'schoolService', 'activeCountService', 'chargeService',
    (scope, rootScope, location, $http, Parent, Child, Employee, Class, School, ActiveCount, Charge) ->
      rootScope.tabName = 'reporting'

      scope.loading = true
      scope.allClasses = []
      scope.allEmployees = []
      scope.allChildren = []
      scope.allParents = []
      scope.allActiveMembers = 0
      scope.allAuthorised = 0
      scope.allMembers = 0
      scope.allVideoMembers = 0
      scope.allActiveParents = 0
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
            scope.allMembers = scope.allMembers + k.active.member
            scope.allVideoMembers = scope.allVideoMembers + k.active.video
            scope.allActiveParents = scope.allActiveParents + k.active.check_in_out
          k.charge = Charge.query school_id: k.school_id, ->
            scope.allAuthorised = scope.allAuthorised + k.charge[0].total_phone_number

        scope.loading = false

      scope.detail = (kg) ->
        location.path "main/school_report/#{kg.school_id}"

  ]

.controller 'OpSchoolReportingCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http', 'parentService', 'childService',
   'schoolEmployeesService', 'classService', 'schoolService', 'adminNewsPreview', 'StatsService', 'activeCountService',
    (scope, rootScope, stateParams, $http, Parent, Child, Employee, Class, School, News, Statistics, ActiveCount) ->
      scope.loading = true

      Chat = Statistics 'session'
      History = Statistics 'history'
      DailyLog = Statistics 'dailylog'

      scope.childrenInSchool = 0
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, ->
          _.forEach scope.kindergarten.classes, (clazz) ->
            clazz.dailyLogs = DailyLog.query school_id: stateParams.school_id, class_id: clazz.class_id, ->
              scope.childrenInSchool = scope.childrenInSchool + clazz.dailyLogs.length

          scope.parents = Parent.query school_id: stateParams.school_id
          scope.children = Child.query school_id: stateParams.school_id, connected: true
          scope.employees = Employee.query school_id: stateParams.school_id
          scope.allNews = News.query school_id: stateParams.school_id, publisher_id: scope.adminUser.id
          scope.allChats = Chat.get school_id: stateParams.school_id
          scope.allHistoryRecords = History.get school_id: stateParams.school_id
          scope.active = ActiveCount.get school_id: stateParams.school_id
          scope.loading = false

  ]




