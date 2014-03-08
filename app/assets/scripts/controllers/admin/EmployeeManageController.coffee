'use strict'

angular.module('kulebaoAdmin').controller 'BulletinManageCtrl',
  ['$scope', '$rootScope', '$location', 'adminNewsService',
   '$stateParams', 'GroupMessage', 'schoolService', '$modal', 'employeeService'
    (scope, $rootScope, $location, adminNewsService, $stateParams, GroupMessage, School, Modal, Employee) ->
      $rootScope.tabName = 'bulletin'

      scope.loading = true

      scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
        scope.adminUser = Employee.get ->
          scope.refresh()

      scope.publish = (news) ->
        news.published = true
        news.$save admin_id: scope.adminUser.id, ->
          scope.$emit 'refreshNews'

      scope.deleteNews = (news) ->
        news.$delete admin_id: scope.adminUser.id, ->
          scope.refresh()

      scope.createNews =  ->
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      scope.edit = (news) ->
        $rootScope.editingNews = angular.copy(news)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      scope.$on 'refreshNews', ->
        scope.refresh()

      scope.refresh = ->
        scope.loading = true
        scope.newsletters = adminNewsService.bind(school_id: $stateParams.kindergarten, admin_id: scope.adminUser.phone).query ->
          scope.loading = false
  ]