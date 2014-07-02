'use strict'

angular.module('kulebaoAdmin').controller 'EmployeesListCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService',
   'uploadService', '$alert', 'employeesManageClassService', 'StatsService', '$location',
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee, Upload, Alert, ClassManager, Stats, location) ->
      $rootScope.tabName = 'employee'

      scope.loading = true

      scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
        scope.adminUser = Employee.get ->
          scope.refresh()

      scope.refresh = ->
        scope.loading = true
        scope.employees = SchoolEmployee.query school_id: $stateParams.kindergarten, ->
          _.forEach scope.employees, (e) ->
            e.subordinates = ClassManager.query school_id: $stateParams.kindergarten, phone: e.phone, ->
              e.displayClasses = (_.map e.subordinates, (s) -> s.name).join(',')
          scope.loading = false

      scope.createEmployee = ->
        new SchoolEmployee
          school_id: parseInt $stateParams.kindergarten
          birthday: '1980-01-01'
          gender: 0
          login_password: ''
          login_name: ''
          workgroup: ''
          workduty: ''
          phone: ''

      scope.addEmployee = ->
        scope.employee = scope.createEmployee()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.edit = (employee) ->
        employee.assignment = Stats('assignment').query school_id: $stateParams.kindergarten, employee_id: employee.id, ->
          employee.assess = Stats('assess').query school_id: $stateParams.kindergarten, employee_id: scope.adminUser.id, ->
            employee.conversation = Stats('conversation').query school_id: $stateParams.kindergarten, employee_id: scope.adminUser.id, ->
              employee.news = Stats('news').query school_id: $stateParams.kindergarten, employee_id: scope.adminUser.id, ->
                scope.employee = angular.copy employee
                scope.currentModal = Modal
                  scope: scope
                  contentTemplate: 'templates/admin/add_employee.html'

      scope.save = (employee) ->
        employee.$save ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          Alert
            title: '老师信息保存失败'
            content: res.data.error_msg
            placement: "top-left"
            container: '.panel-body'

      scope.isDuplicated = (employee) ->
        return false if employee.phone is undefined || employee.phone.length < 10
        undefined isnt _.find scope.employees, (e) ->
          e.phone == employee.phone && e.id != employee.id

      scope.delete = (employee) ->
        employee.$delete ->
          scope.refresh()

      scope.uploadPic = (employee, pic) ->
        scope.uploading = true
        Upload pic, (url) ->
          scope.$apply ->
            employee.portrait = url if url isnt undefined
            scope.uploading = false
        , scope.adminUser.id

      scope.deletable = (user)->
        undefined isnt _.find ['principal', 'operator'], (u) -> user.privilege_group == u

      scope.goScore = ->
        location.path location.path().replace(/\/detail$/, '/score')

      scope.goInfo = ->
        location.path location.path().replace(/\/score$/, '/detail')

  ]

angular.module('kulebaoAdmin').controller 'EmployeesScoreCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService',
   'uploadService', '$alert', 'StatsService',
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee, Upload, Alert, Stats) ->

      scope.refresh = ->
        scope.loading = true
        scope.employees = SchoolEmployee.query school_id: $stateParams.kindergarten, ->
          scope.assignmentStats = Stats('assignment').query school_id: $stateParams.kindergarten, ->
            _.forEach scope.employees, (e) ->
              e.assignment = _.find scope.assignmentStats, (s) -> e.id == s.employee_id
              e.assignment = count: 0 unless e.assignment?
          scope.assessStats = Stats('assess').query school_id: $stateParams.kindergarten, ->
            _.forEach scope.employees, (e) ->
              e.assess = _.find scope.assessStats, (s) -> e.id == s.employee_id
              e.assess = count: 0 unless e.assess?
          scope.conversationStats = Stats('conversation').query school_id: $stateParams.kindergarten, ->
            _.forEach scope.employees, (e) ->
              e.conversation = _.find scope.conversationStats, (s) -> e.id == s.employee_id
              e.conversation = count: 0 unless e.conversation?
          scope.newsStats = Stats('news').query school_id: $stateParams.kindergarten, ->
            _.forEach scope.employees, (e) ->
              e.news = _.find scope.newsStats, (s) -> e.id == s.employee_id
              e.news = count: 0 unless e.news?

          scope.loading = false

      safeCount = (e) ->
        try
          e.count
        catch error
          0

      scope.countScore = (employee) ->
        safeCount(employee.assignment) + safeCount(employee.news) + safeCount(employee.conversation) + safeCount(employee.assess)

      scope.refresh()
  ]
