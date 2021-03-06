'use strict'

angular.module('kulebaoAdmin').controller 'BulletinManageCtrl',
  ['$scope', '$rootScope', '$state', '$timeout',
   '$stateParams', '$modal', 'imageCompressService', 'eligibleCheckService',
    (scope, $rootScope, $state, $timeout, stateParams, Modal, Compress, Eligible) ->
      $rootScope.tabName = 'bulletin'
      scope.heading = '全园和班级的通知公告都可以在这里发布'

      $rootScope.loading = true
      scope.current_class = parseInt(stateParams.class)

      scope.classesScope = angular.copy scope.kindergarten.classes
      scope.classesScope.unshift class_id: 0, name: '全校'
      scope.eligibleClassesScope = []
      scope.adminUser.ineligibleClasses = [0]
      Eligible.query school_id: stateParams.kindergarten, id: scope.adminUser.uid, (data)->
        scope.adminUser.ineligibleClasses = data
        if data.length == 0
          scope.eligibleClassesScope = scope.classesScope
        else
          scope.eligibleClassesScope = _.reject scope.kindergarten.classes, (c) -> _.any data, (d) -> d.class_id == c.class_id
      $state.go('kindergarten.bulletin.class.list', {kindergarten: stateParams.kindergarten, class: 0}) unless $state.is 'kindergarten.bulletin.class.list'

      scope.navigateTo = (c) ->
        if c.class_id != scope.current_class || !$state.is 'kindergarten.bulletin.class.list'
          $rootScope.loading = true
          $timeout ->
            $state.go 'kindergarten.bulletin.class.list', {kindergarten: stateParams.kindergarten, class: c.class_id}

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

      scope.schoolLevelReadOnly = -> scope.adminUser.ineligibleClasses.length > 0 && scope.current_class == 0
  ]

.controller 'BulletinCtrl',
  ['$scope', '$rootScope', '$q', 'adminNewsServiceV2',
   '$stateParams', '$modal', 'adminNewsPreview', 'senderService', 'newsReadService', 'parentService', 'schoolConfigService', 
    'schoolConfigExtractService', 'schoolSmsService', '$alert',
    (scope, $rootScope, $q, AdminNews, stateParams, Modal, NewsPreview, Sender, NewsRead, Parent, SchoolConfig, ConfigExtract,
    SmsConfig, Alert) ->
      scope.totalItems = 0
      scope.currentPage = 1
      scope.maxSize = 5
      scope.itemsPerPage = 8
      scope.allTags = ['作业', '活动', '备忘']

      scope.refresh = ->
        page = scope.currentPage
        $rootScope.loading = true
        scope.preview = NewsPreview.query
          school_id: stateParams.kindergarten
          publisher_id: scope.adminUser.id
          class_id: stateParams.class
          restrict: true, (data) ->
            scope.preview = data.reverse()
            scope.totalItems = scope.preview.length
            startIndex = (page - 1) * scope.itemsPerPage
            last = scope.preview[startIndex...startIndex + scope.itemsPerPage][0].id if scope.preview.length > 0
            scope.newsletters = AdminNews.query
              school_id: stateParams.kindergarten
              publisher_id: scope.adminUser.id
              class_id: stateParams.class
              restrict: true
              to: last + 1 || last
              most: scope.itemsPerPage, (all) ->
                $rootScope.loading = false
                _.each all, (one) -> one.publisher = Sender.bind(school_id: stateParams.kindergarten, id: one.publisher_id, type: 't').get() if one.publisher_id?

      scope.refresh()

      scope.$watch 'currentPage', (newPage, oldPage) ->
        scope.refresh(newPage) if newPage isnt oldPage

      scope.publish = (news) ->
        news.published = true
        news.publisher_id = scope.adminUser.id
        news.$save ->
          news.publisher_id = scope.adminUser.id
          news.publisher =
            name: scope.adminUser.name
          scope.smsConfig = {}

      scope.deleteNews = (news) ->
        news.publisher_id = scope.adminUser.id
        news.$delete ->
          scope.refresh()

      firstAvailableClassId = ->
        if scope.adminUser.ineligibleClasses.length > 0
          _.first _.pluck scope.eligibleClassesScope, 'class_id'
        else
          scope.current_class

      scope.createNews = ->
        new AdminNews
          school_id: scope.kindergarten.school_id
          class_id: firstAvailableClassId()
          publisher_id: scope.adminUser.id
          published: false
          tags: []

      scope.create = ->
        scope.news = scope.createNews()
        refreshSmsPrivilege()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      scope.edit = (news) ->
        scope.news = angular.copy(news)
        scope.news.images = _.map news.images, (image) -> url: image
        refreshSmsPrivilege()
        scope.news.sms_required = scope.news.sms && scope.news.sms.length > 0
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_news.html'

      refreshSmsConfig = (classId)->
        SmsConfig.query school_id: scope.kindergarten.school_id, classIds: classId, (data) ->
          scope.smsConfig = data[0]
          if scope.smsConfig.consumers == 0
            scope.news.sms_required = false
      scope.$watch 'news.sms_required', (n, o) ->
        unless scope.eligibleToSendSms || !n
          scope.news.sms_required = false
          Alert
            title: "未开通"
            content: '贵园暂未开通短信服务，请联系幼乐宝。'
            placement: "top"
            type: "danger"
            container: '.modal-dialog .panel-body'
            duration: 3
        else
          refreshSmsConfig(scope.news.class_id) if n && !scope.smsConfig.available?

      scope.$watch 'news.class_id', (n, o) ->
        refreshSmsConfig(scope.news.class_id) if scope.news && scope.news.sms_required


      refreshSmsPrivilege = ->
        SchoolConfig.get school_id: scope.kindergarten.school_id, (data) ->
          account = ConfigExtract data['config'], 'smsPushAccount', ''
          password = ConfigExtract data['config'], 'smsPushPassword', ''
          signature = ConfigExtract data['school_customized'], 'smsPushSignature', ''
          globalSwitch = ConfigExtract data['school_customized'], 'switch_sms_on_card_wiped', ''
          scope.eligibleToSendSms = account.length > 0 && password.length > 0 && signature.length > 0 && globalSwitch.length > 0
        scope.smsConfig = {}


      scope.smsConsumePredicate = (sms) ->
        return 0 if sms is undefined
        smsConfig = scope.smsConfig
        Math.ceil(sms.length / smsConfig.pagination) * smsConfig.consumers
          
      firstPageOrCurrentPage = (news) ->
        if news.news_id?
          ->
            scope.refresh()
        else
          ->
            scope.currentPage = 1
            scope.refresh()

      scope.closeDialog = (news) ->
        scope.currentModal.hide()
        scope.navigateTo(class_id: news.class_id) unless news.class_id == parseInt stateParams.class
        firstPageOrCurrentPage(news)()

      scope.save = (news) ->
        goPage = firstPageOrCurrentPage(news)
        news.images = _.pluck news.images, 'url'
        delete news.sms unless news.sms_required
        news.$save ->
          scope.navigateTo(class_id: news.class_id) unless news.class_id == parseInt stateParams.class
          goPage()
          scope.currentModal.hide()
          scope.smsConfig = {}

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
        scope.currentModal.hide() if scope.currentModal?
        queue = [Parent.query(school_id: stateParams.kindergarten, class_id: if news.class_id == 0 then undefined else news.class_id).$promise,
                               NewsRead.query(news).$promise
                ]
        $q.all(queue).then (q) ->
          allReaders = _.map (_.reject q[1], (r) -> !r.id?), (d) -> _.extend(d, read:true)
          unreadParents = _.reject (_.reject q[0], (r) -> !r.id?), (p) -> _.some allReaders, (r) -> r.parent_id == p.parent_id
          scope.news_feedbacks =  _.union allReaders, unreadParents
        scope.current_news = news
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/news_feedbacks.html'

      scope.printTags = (tags) ->
        if tags? then tags.join ', ' else ''

      $rootScope.loading = false


      scope.onUploadSuccess = (url) ->
        scope.$apply ->
          scope.news.images.push url: url if url isnt undefined
          scope.disableUploading = scope.dynamicDisable(scope.news)
          scope.buttonLabel = scope.dynamicLabel(scope.news)

      scope.dynamicLabel = (message)->
        if message.images.length == 0 then '添加' else '继续添加'

      scope.dynamicDisable = (message) ->
        message.logos && message.images.length > 8
  ]