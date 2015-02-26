'use strict'

angular.module('kulebaoAdmin').controller 'ClassesManagementCtrl',
  ['$scope', '$rootScope', '$stateParams', '$modal', '$q', 'schoolEmployeesService',
   'classService', '$alert', 'classManagerService',
    (scope, $rootScope, $stateParams, Modal, $q, SchoolEmployee, Class, Alert, ClassManager) ->
      $rootScope.tabName = 'classes'

      scope.page =
        loading : true

      SchoolEmployee.query school_id: $stateParams.kindergarten, (employees) ->
        scope.employees = _.map employees, (e) ->
          e.value = e.name
          e

      scope.refresh = ->
        scope.page.loading = true
        scope.classes = Class.query school_id: $stateParams.kindergarten, ->
          all = _.map scope.classes, (c) ->
            $q (resolve, reject) ->
              manager = ClassManager.query c, ->
                c.managers = _.map manager, (m) -> m.name
                resolve(c.managers)
          $q.all(all).then ->
            scope.page.loading = false


      scope.refresh()

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
        scope.page.loading = true
        managers = clazz.managers
        clazz.$save ->
          scope.$emit 'classUpdated'
          _.forEach managers, (m) ->
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
        scope.page.loading = true
        clazz.$delete ->
          scope.$emit 'classUpdated'
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
          scope.refresh()

      scope.nameOf = (employeeId) ->
        employee = _.find scope.employees, (e) ->
          e.id == employeeId
        employee.name if employee?
  ]