'use strict'

angular.module('kulebaoAdmin').controller 'BulletinManageCtrl',
  ['$scope', '$rootScope', '$location',
   '$stateParams', '$modal', 'imageCompressService',
    (scope, $rootScope, location, stateParams, Modal, Compress) ->
      $rootScope.tabName = 'bulletin'
      scope.heading = '全园和班级的通知公告都可以在这里发布'

      scope.current_class = parseInt(stateParams.class)

      scope.loading = true

      scope.classesScope = angular.copy scope.kindergarten.classes
      scope.classesScope.unshift class_id: 0, name: '全校'
      location.path "#{location.path()}/class/0/list" if (location.path().indexOf('/class/') < 0)

      scope.navigateTo = (c) ->
        location.path("kindergarten/#{stateParams.kindergarten}/bulletin/class/#{c.class_id}/list")

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)
  ]

.controller 'BulletinCtrl',
  ['$scope', '$rootScope', '$q', 'adminNewsServiceV2',
   '$stateParams', '$modal', 'adminNewsPreview', 'senderService', 'newsReadService', 'parentService',
    (scope, $rootScope, $q, adminNewsService, stateParams, Modal, NewsPreivew, Sender, NewsRead, Parent) ->
      scope.totalItems = 0
      scope.currentPage = 1
      scope.maxSize = 5
      scope.itemsPerPage = 8
      scope.allTags = ['作业', '活动', '备忘']

      scope.refresh = ->
        page = scope.currentPage
        scope.loading = true
        scope.preview = NewsPreivew.query
          school_id: stateParams.kindergarten
          publisher_id: scope.adminUser.id
          class_id: stateParams.class
          restrict: true, (data) ->
            scope.preview = data.reverse()
            scope.totalItems = scope.preview.length
            startIndex = (page - 1) * scope.itemsPerPage
            last = scope.preview[startIndex...startIndex + scope.itemsPerPage][0].id if scope.preview.length > 0
            scope.newsletters = adminNewsService.query
              school_id: stateParams.kindergarten
              publisher_id: scope.adminUser.id
              class_id: stateParams.class
              restrict: true
              to: last + 1 || last
              most: scope.itemsPerPage, (all) ->
                scope.loading = false
                _.forEach all, (one) -> one.publisher = Sender.bind(school_id: stateParams.kindergarten, id: one.publisher_id, type: 't').get() if one.publisher_id?

      scope.refresh()

      scope.$watch 'currentPage', (newPage, oldPage) ->
        scope.refresh(newPage) if newPage isnt oldPage

      scope.publish = (news) ->
        news.published = true
        news.publisher_id = scope.adminUser.id
        news.$save ->
          scope.refresh()

      scope.deleteNews = (news) ->
        news.publisher_id = scope.adminUser.id
        news.$delete ->
          scope.refresh()

      scope.createNews = ->
        new adminNewsService
          school_id: scope.kindergarten.school_id
          class_id: scope.current_class
          publisher_id: scope.adminUser.id

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

      firstPageOrCurrentPage = (news) ->
        if news.news_id?
          ->
            scope.refresh()
        else
          ->
            scope.currentPage = 1
            scope.refresh()

      scope.save = (news) ->
        goPage = firstPageOrCurrentPage(news)
        news.publisher_id = scope.adminUser.id
        news.$save ->
          goPage()
          scope.currentModal.hide()

      scope.remove = (news) ->
        news.publisher_id = scope.adminUser.id
        news.$delete ->
          scope.refresh()
          scope.currentModal.hide()

      scope.nameOf = (class_id) ->
        return '全校' if class_id == 0 || scope.kindergarten is undefined
        clazz = _.find scope.classesScope, (c) ->
          c.class_id == class_id
        clazz.name if clazz isnt undefined

      scope.showFeedbacks = (news) ->
        scope.currentModal.hide()
        queue = [Parent.query(school_id: stateParams.kindergarten, class_id: if news.class_id == 0 then undefined else news.class_id).$promise,
                               NewsRead.query(news).$promise
                ]
        $q.all(queue).then (q) ->
          allReaders = _.map q[1], (d) -> _.extend(d, read:true)
          unreadParents = _.reject q[0], (p) -> _.some allReaders, (r) -> r.parent_id == p.parent_id
          scope.news_feedbacks =  _.union allReaders, unreadParents
        scope.current_news = news
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/news_feedbacks.html'

      scope.printTags = (tags) ->
        tags.join ', '
  ]