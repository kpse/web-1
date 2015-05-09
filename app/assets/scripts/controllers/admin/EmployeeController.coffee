'use strict'

angular.module('kulebaoAdmin').controller 'EmployeesListCtrl',
  ['$scope', '$rootScope', '$stateParams', '$modal', 'schoolEmployeesService',
   'uploadService', '$alert', 'employeesManageClassService', 'StatsService', '$location', '$q'
    (scope, $rootScope, $stateParams, Modal, SchoolEmployee, Upload, Alert, ClassManager, Stats, location, $q) ->
      $rootScope.tabName = 'employee'

      scope.refresh = ->
        scope.loading = true
        scope.employees = SchoolEmployee.query school_id: $stateParams.kindergarten, (data)->
          scope.employees = _.reject data, (employee) -> employee.id == scope.adminUser.id
          all = _.map scope.employees, (e) ->
            $q (resolve, reject) ->
              e.subordinates = ClassManager.query school_id: $stateParams.kindergarten, phone: e.phone, ->
                e.displayClasses = (_.map e.subordinates, (s) -> s.name).join(',')
                resolve(e.subordinates)
          $q.all(all).then ->
            scope.loading = false

      scope.refresh()

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
            container: '.modal-body.panel-body'

      scope.delete = (employee) ->
        employee.$delete ->
            scope.refresh()
          , (res) ->
            Alert
              title: '老师信息删除失败'
              content: res.data.error_msg
              placement: "top-left"
              container: '.panel-body'


      scope.buttonLabel = '上传头像'

      scope.deletable = (user)->
        undefined isnt _.find ['principal', 'operator'], (u) -> user.privilege_group == u

      scope.goScore = ->
        location.path location.path().replace(/\/detail$/, '/score')

      scope.goInfo = ->
        location.path location.path().replace(/\/score$/, '/detail')

      scope.groupDisplayName = (group) ->
        if group == 'teacher' then '普通老师' else '校长'

      scope.checkAll = (check) ->
        _.forEach scope.employees, (r) ->
          r.checked = check

      scope.multipleDelete = ->
        checked = _.filter scope.employees, (r) ->
          r.checked? && r.checked == true
        queue = _.map checked, (employee) ->
          SchoolEmployee.delete(school_id: $stateParams.kindergarten, phone: employee.phone).$promise
        all = $q.all queue
        all.then (q) ->
          scope.refresh()

      scope.hasSelection = (employees) ->
        _.some employees, (r) ->
          r.checked? && r.checked == true

      scope.singleSelection = (employee) ->
        allChecked = _.every scope.employees, (r) ->
          r.checked? && r.checked == true
        scope.selection.allCheck = allChecked

      scope.selection =
        allCheck: false

      scope.checkName = (employee, form) ->
        form.$setValidity 'unique', !_.any scope.employees, (e) -> e.name == employee.name
  ]

.controller 'EmployeesScoreCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService',
   '$alert', 'StatsService',
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee, Alert, Stats) ->

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
