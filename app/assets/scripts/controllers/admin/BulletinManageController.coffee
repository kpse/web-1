'use strict'

angular.module('kulebaoAdmin').controller 'BulletinManageCtrl',
  ['$scope', '$rootScope', '$location', 'adminNewsService',
   '$stateParams', 'schoolService', '$modal', 'employeeService', 'classService',
    (scope, $rootScope, location, adminNewsService, stateParams, School, Modal, Employee, Class) ->
      $rootScope.tabName = 'bulletin'
      scope.heading = '按范围发布公告'

      scope.loading = true

      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
            scope.kindergarten.classes.unshift class_id: 0, name: '全校'
            location.path(location.path() + '/class/0/list') if (location.path().indexOf('/class/') < 0)

      scope.navigateTo = (s) ->
        location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + s.class_id + '/list')
  ]

angular.module('kulebaoAdmin').controller 'BulletinCtrl',
  ['$scope', '$rootScope', 'adminNewsService',
   '$stateParams', 'schoolService', '$modal', 'employeeService', 'classService', 'adminNewsPreview',
    (scope, $rootScope, adminNewsService, stateParams, School, Modal, Employee, Class, NewsPreivew) ->
      scope.current_class = parseInt(stateParams.class)

      scope.totalItems = 0
      scope.currentPage = 1
      scope.maxSize = 5
      scope.itemsPerPage = 8

      scope.refresh = (to)->
        scope.loading = true
        scope.preview = NewsPreivew.query
          school_id: stateParams.kindergarten
          admin_id: scope.adminUser.phone
          class_id: stateParams.class
          restrict: true, ->
            scope.preview = scope.preview.reverse()
            scope.totalItems = scope.preview.length
            scope.newsletters = adminNewsService.query
              school_id: stateParams.kindergarten
              admin_id: scope.adminUser.phone
              class_id: stateParams.class
              restrict: true
              to: to
              most: scope.itemsPerPage, ->
                scope.loading = false

      indexRange = (page) ->
        startIndex = (page - 1) * scope.itemsPerPage
        scope.preview[startIndex...startIndex + scope.itemsPerPage]

      scope.onSelectPage = (page) ->
        last = indexRange(page)[0].id
        console.log last
        scope.refresh(last + 1)

      scope.$on 'go_page_1', ->
        scope.onSelectPage(1)

      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
            scope.kindergarten.classes.unshift class_id: 0, name: '全校'
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
          class_id: scope.current_class

      scope.create = ->
        scope.news = scope.createNews()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      scope.edit = (news) ->
        scope.news = angular.copy(news)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      scope.save = (news) ->
        news.$save admin_id: scope.adminUser.id, ->
          scope.$emit 'go_page_1'
          scope.currentModal.hide()

      scope.remove = (news) ->
        news.$delete admin_id: scope.adminUser.id, ->
          scope.$emit 'go_page_1'
          scope.currentModal.hide()

      scope.nameOf = (class_id) ->
        return '全校' if class_id == 0
        clazz = _.find scope.kindergarten.classes, (c) ->
          c.class_id == class_id
        clazz.name if clazz isnt undefined
  ]