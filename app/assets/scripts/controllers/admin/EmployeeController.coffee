'use strict'

angular.module('kulebaoAdmin').controller 'EmployeesListCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService'
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee) ->
      $rootScope.tabName = 'employee'

      scope.loading = true

      scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
        scope.adminUser = Employee.get ->
          scope.refresh()

      scope.refresh = ->
        scope.loading = true
        scope.employees = SchoolEmployee.query school_id: $stateParams.kindergarten, ->
          scope.loading = false
  ]