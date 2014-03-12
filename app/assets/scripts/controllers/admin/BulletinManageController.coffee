'use strict'

angular.module('kulebaoAdmin').controller 'BulletinManageCtrl',
  ['$scope', '$rootScope', '$location', 'adminNewsService',
   '$stateParams', 'GroupMessage', 'schoolService', '$modal', 'employeeService', 'classService',
    (scope, $rootScope, $location, adminNewsService, $stateParams, GroupMessage, School, Modal, Employee, Class) ->
      $rootScope.tabName = 'bulletin'

      scope.loading = true

      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: $stateParams.kindergarten, ->
            scope.refresh()

      scope.publish = (news) ->
        news.published = true
        news.$save admin_id: scope.adminUser.id, ->
          scope.refresh()

      scope.deleteNews = (news) ->
        news.$delete admin_id: scope.adminUser.id, ->
          scope.refresh()

      scope.createNews = ->
        new adminNewsService
          school_id: scope.kindergarten.school_id
          admin_id: scope.adminUser.id

      scope.addNews =  ->
        scope.news = scope.createNews()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      scope.edit = (news) ->
        scope.news = angular.copy(news)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      scope.refresh = ->
        scope.loading = true
        scope.newsletters = adminNewsService.query school_id: $stateParams.kindergarten, admin_id: scope.adminUser.phone, ->
          scope.loading = false

      scope.save = (news) ->
        news.$save admin_id: scope.adminUser.id,  ->
          scope.refresh()
          scope.currentModal.hide()

      scope.remove = (news) ->
        news.$delete admin_id: scope.adminUser.id, ->
          scope.refresh()
          scope.currentModal.hide()

      scope.nameOf = (class_id) ->
        clazz = _.find scope.kindergarten.classes, (c) ->
          c.class_id == class_id
        clazz.name if clazz isnt undefined
  ]