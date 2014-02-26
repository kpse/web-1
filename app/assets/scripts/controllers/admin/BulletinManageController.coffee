'use strict'

angular.module('kulebaoAdmin').controller 'BulletinManageCtrl',
  ['$scope', '$rootScope', '$location', 'adminNewsService',
   '$stateParams', 'GroupMessage', 'schoolService', '$modal'
    (scope, $rootScope, $location, adminNewsService, $stateParams, GroupMessage, School, Modal) ->
      $rootScope.tabName = 'bulletin'

      scope.loading = true
      scope.adminUser =
        id: 1
        name: '学校某老师'

      scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
        scope.refresh()

      scope.publish = (news) ->
        news.published = true
        news.$save admin_id: scope.adminUser.id, ->
          scope.$emit 'refreshNews'


      scope.hidden = (news) ->
        news.published = false
        news.$save(school_id: scope.kindergarten.school_id, news_id: news.news_id, admin_id: scope.adminUser.id)

      scope.deleteNews = (news) ->
        news.$delete(school_id: scope.kindergarten.school_id, news_id: news.news_id, admin_id: scope.adminUser.id)
        scope.newsletters = scope.newsletters.filter (x) ->
          x != news

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
        scope.loading = true
        scope.newsletters = adminNewsService.bind(school_id: scope.kindergarten.school_id, admin_id: scope.adminUser.id).query ->
          scope.loading = false

      scope.refresh = ->
        scope.loading = true
        scope.newsletters = adminNewsService.bind(school_id: scope.kindergarten.school_id, admin_id: scope.adminUser.id).query ->
          scope.loading = false
  ]