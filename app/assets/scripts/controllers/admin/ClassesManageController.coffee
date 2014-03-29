'use strict'

angular.module('kulebaoAdmin').controller 'ClassesManagementCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService',
   'classService', '$alert', 'classManagerService',
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee, Class, Alert, ClassManager) ->
      $rootScope.tabName = 'classes'

      scope.loading = true

      scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
        employees = SchoolEmployee.query school_id: $stateParams.kindergarten, ->
          scope.employees = _.map employees, (e) ->
            e.value = e.name
            e
        scope.adminUser = Employee.get ->
          scope.refresh()

      scope.refresh = ->
        scope.loading = true
        scope.classes = Class.query school_id: $stateParams.kindergarten, ->
          _.forEach scope.classes, (c) ->
            manager = ClassManager.query c, ->
              c.managers = _.map manager, (m) -> m.name
          scope.loading = false

      scope.createClass = ->
        new Class
          school_id: parseInt $stateParams.kindergarten
          name: ''

      scope.addClass = ->
        scope.currentClass = scope.createClass()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_class.html'

      scope.edit = (clazz) ->
        scope.currentClass = angular.copy(clazz)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_class.html'

      getManager = (m) ->
        _.find scope.employees, (e) -> e.name == m

      scope.save = (clazz) ->
        clazz.$save ->
          _.forEach clazz.managers, (m) ->
            cm = new ClassManager(getManager(m))
            cm.class_id = clazz.class_id
            cm.$save()
          scope.refresh()
          scope.currentModal.hide()

      scope.isDuplicated = (clazz) ->
        return false if clazz.name is undefined
        undefined isnt _.find scope.classes, (c) ->
          c.name == clazz.name && c.class_id != clazz.class_id

      scope.delete = (clazz) ->
        clazz.$delete ->
          scope.refresh()
        , (res) ->
          Alert
            title: '无法删除'
            content: res.data.error_msg
            placement: "top-left"
            type: "danger"
            show: true
            container: '.panel-body'
            duration: 3

      scope.nameOf = (employee_id) ->
        employee = _.find scope.employees, (e) ->
          e.id == employee_id
        employee.name if employee
  ]