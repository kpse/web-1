'use strict'

angular.module('kulebaoAdmin').controller 'ClassesManagementCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$modal', 'employeeService', 'schoolEmployeesService',
   'classService', '$alert',
    (scope, $rootScope, $stateParams, School, Modal, Employee, SchoolEmployee, Class, Alert) ->
      $rootScope.tabName = 'classes'

      scope.loading = true

      scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
        scope.adminUser = Employee.get ->
          scope.refresh()
        scope.employees = SchoolEmployee.query school_id: $stateParams.kindergarten

      scope.refresh = ->
        scope.loading = true
        scope.classes = Class.query school_id: $stateParams.kindergarten, ->
          scope.loading = false

      scope.createClass = ->
        new Class
          school_id: parseInt $stateParams.kindergarten
          name: ''
          class_id: nextId(scope.classes)

      nextId = (classes)->
        23 + _.max _.map classes, (c) ->
          c.class_id

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

      scope.save = (clazz) ->
        clazz.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.isDuplicated = (clazz) ->
        return false if clazz.name is undefined
        undefined isnt _.find scope.classes, (c) ->
          c.name == clazz.name && c.class_id != clazz.class_id

      scope.delete = (clazz) ->
        clazz.$delete ->
          scope.refresh()
        , ->
          Alert
            title: '无法删除'
            content: '该班级中还有学生存在。'
            placement: "top-left"
            type: "danger"
            show: true
            container: '.panel-body'
            duration: 3
  ]