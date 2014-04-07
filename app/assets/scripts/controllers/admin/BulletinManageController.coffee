'use strict'

angular.module('kulebaoAdmin').controller 'BulletinManageCtrl',
  ['$scope', '$rootScope', '$location', 'adminNewsService',
   '$stateParams', 'schoolService', '$modal', 'employeeService', 'classService',
    (scope, $rootScope, location, adminNewsService, stateParams, School, Modal, Employee, Class) ->
      $rootScope.tabName = 'bulletin'
      scope.heading = '全园和班级的通知公告都可以在这里发布'

      scope.current_class = parseInt(stateParams.class)

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
   '$stateParams', 'schoolService', '$modal', 'employeeService', 'classService', 'adminNewsPreview', 'uploadService',
    (scope, $rootScope, adminNewsService, stateParams, School, Modal, Employee, Class, NewsPreivew, Upload) ->
      scope.totalItems = 0
      scope.currentPage = 1
      scope.maxSize = 5
      scope.itemsPerPage = 8

      scope.refresh = (page)->
        page = page || 1
        scope.loading = true
        scope.preview = NewsPreivew.query
          school_id: stateParams.kindergarten
          admin_id: scope.adminUser.phone
          class_id: stateParams.class
          restrict: true, ->
            scope.preview = scope.preview.reverse()
            scope.totalItems = scope.preview.length
            startIndex = (page - 1) * scope.itemsPerPage
            last = scope.preview[startIndex...startIndex + scope.itemsPerPage][0].id if scope.preview.length > 0
            scope.newsletters = adminNewsService.query
              school_id: stateParams.kindergarten
              admin_id: scope.adminUser.phone
              class_id: stateParams.class
              restrict: true
              to: last + 1 || last
              most: scope.itemsPerPage, ->
                scope.loading = false

      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
            scope.kindergarten.classes.unshift class_id: 0, name: '全校'
            scope.refresh()

      scope.onSelectPage = (page) ->
        scope.refresh(page)

      scope.$on 'go_page_1', ->
        scope.onSelectPage()

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
        return '全校' if class_id == 0 || scope.kindergarten is undefined
        clazz = _.find scope.kindergarten.classes, (c) ->
          c.class_id == class_id
        clazz.name if clazz isnt undefined

      scope.uploadPic = (news, pic) ->
        scope.uploading = true
        Upload pic, (url) ->
          scope.$apply ->
            news.image = url if url isnt undefined
            scope.uploading = false
        , scope.adminUser.id
  ]