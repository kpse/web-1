'use strict'

angular.module('kulebaoAdmin').controller 'ClassesManagementCtrl',
  ['$scope', '$rootScope', '$stateParams', '$modal', '$q', 'schoolEmployeesService',
   'classService', '$alert', 'classManagerService', 'schoolChatGroupService', 'schoolConfigService',
    (scope, $rootScope, $stateParams, Modal, $q, SchoolEmployee, Class, Alert, ClassManager, Chat, Config) ->
      $rootScope.tabName = 'classes'

      $rootScope.loading = true

      SchoolEmployee.query school_id: $stateParams.kindergarten, (employees) ->
        scope.employees = _.map employees, (e) ->
          e.value = e.name
          e

      scope.refresh = ->
        $rootScope.loading = true
        scope.classes = Class.query school_id: $stateParams.kindergarten, ->
          all = _.map scope.classes, (c) ->
            $q (resolve, reject) ->
              manager = ClassManager.query c, ->
                c.managers = _.map manager, (m) -> m.name
                resolve(c.managers)
          all.push Config.get(school_id: $stateParams.kindergarten).$promise
          $q.all(all).then (q)->
            $rootScope.loading = false
            chatStatusResult = (_.last q)
            chatGroupStatus = _.find (chatStatusResult.config || []), (c) -> c.name == 'schoolGroupChat'
            scope.chatEnabled = chatGroupStatus.value != 'false'

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
        $rootScope.loading = true
        managers = clazz.managers
        clazz.$save ->
          scope.$emit 'classUpdated'
          _.each managers, (m) ->
            cm = new ClassManager(getManager(m))
            cm.class_id = clazz.class_id
            cm.$save()

          scope.refresh()
          scope.currentModal.hide()

      scope.isDuplicated = (clazz) ->
        return false if clazz.name is undefined
        _.any scope.classes, (c) ->
          c.name == clazz.name && c.class_id != clazz.class_id

      scope.delete = (clazz) ->
        $rootScope.loading = true
        clazz.$delete ->
          scope.$emit 'classUpdated'
          scope.refresh()
        , (res) ->
          Alert
            title: '无法删除'
            content: res.data.error_msg
            placement: "top"
            type: "danger"
            show: true
            container: '.panel-body'
            duration: 3
          scope.refresh()

      scope.nameOf = (employeeId) ->
        employee = _.find scope.employees, (e) ->
          e.id == employeeId
        employee.name if employee?

      scope.enableChatGroup = ->
        scope.chatEnabled = true
        Chat.save(school_id: $stateParams.kindergarten, name: 'schoolGroupChat', value: 'true')
      scope.disableChatGroup = ->
        scope.chatEnabled = false
        Chat.save(school_id: $stateParams.kindergarten, name: 'schoolGroupChat', value: 'false')
  ]