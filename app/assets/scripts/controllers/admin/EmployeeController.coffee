'use strict'

angular.module('kulebaoAdmin').controller 'EmployeesListCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService', 'uploadService',
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee, Upload) ->
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
          school_id: $stateParams.kindergarten
          birthday: '1980-01-01'
          gender: 0

      scope.addEmployee = ->
        scope.employee = scope.createEmployee()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.edit = (employee) ->

      scope.isDuplicated = (phone) ->
        return false if phone is undefined || phone.length < 10
        undefined isnt _.find scope.employees, (e) ->
          e.phone == phone

      scope.delete = (employee) ->
        employee.$delete ->
          scope.refresh()

      scope.uploadPic = (employee, pic) ->
        Upload pic, (url) ->
          scope.$apply ->
            employee.portrait = url if url isnt undefined


  ]