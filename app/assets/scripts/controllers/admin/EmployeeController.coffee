'use strict'

angular.module('kulebaoAdmin').controller 'EmployeesListCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService',
   'uploadService', '$alert',
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee, Upload, Alert) ->
      $rootScope.tabName = 'employee'

      scope.loading = true

      scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
        scope.adminUser = Employee.get ->
          scope.refresh()

      scope.refresh = ->
        scope.loading = true
        scope.employees = SchoolEmployee.query school_id: $stateParams.kindergarten, ->
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
            title: '创建失败'
            content: res.data.error_msg
            placement: "top-left"
            type: "danger"
            show: true
            container: '.panel-body'
            duration: 3

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


  ]