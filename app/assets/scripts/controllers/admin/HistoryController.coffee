'use strict'

angular.module('kulebaoAdmin')
.controller 'HistoryListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'accessClassService',
    (scope, rootScope, stateParams, location, AccessClass) ->
      rootScope.tabName = 'history'
      scope.heading = '记录小朋友成长的各种瞬间'

      AccessClass(scope.kindergarten.classes)

  ]

.controller 'HistoryClassesSelectionCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$timeout',
    '$state', 'parentService', 'historyService', 'childService', 'senderService', 'readRecordService',
    (scope, rootScope, stateParams, $timeout, $state, Parent, Chat, Child, Sender, ReaderLog) ->
      rootScope.loading = true
      scope.current_class = parseInt(stateParams.class_id)

      scope.children = Child.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true).query ->
        _.forEach scope.children, (child) ->
          child.messages = Chat.bind(school_id: stateParams.kindergarten, topic: child.child_id, most: 1).query ->
            child.lastMessage = child.messages[0]
            child.lastMessage.isRead = true if child.lastMessage isnt undefined
            if child.lastMessage isnt undefined
              child.lastMessage.sender.info = Sender.bind(school_id: stateParams.kindergarten, id: child.lastMessage.sender.id, type: child.lastMessage.sender.type).get ->
                child.lastMessage.sender.name = child.lastMessage.sender.info.name
                child.lastMessage.sender.read_record = ReaderLog.bind(school_id: stateParams.kindergarten, topic: child.child_id, reader: scope.adminUser.id).get ->
                  child.lastMessage.isRead = child.lastMessage.sender.read_record.session_id >= child.lastMessage.id

        rootScope.loading = false

      scope.navigateTo = (c) ->
        if c.class_id != scope.current_class || !$state.is 'kindergarten.history.class.list'
          rootScope.loading = true
          $timeout ->
            $state.go 'kindergarten.history.class.list', {kindergarten: stateParams.kindergarten, class_id: c.class_id}

  ]

.controller 'HistoryInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$timeout', '$state',
    (scope, rootScope, stateParams, $timeout, $state) ->
      rootScope.loading = true

      scope.goDetail = (child) ->
        rootScope.loading = true
        $timeout ->
          $state.go 'kindergarten.history.class.child', {kindergarten: stateParams.kindergarten, class_id: child.class_id, child_id: child.child_id}

      rootScope.loading = false
  ]

.controller 'HistoryCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', '$http', 'historyService', 'childService', '$modal',
    '$popover', '$tooltip', 'employeeService', 'senderService', 'readRecordService', 'historyShareService',
    (scope, rootScope, stateParams, location, $http, Message, Child, Modal, Popover, Tooltip, Employee, Sender, ReaderLog, Share) ->
      scope.adminUser = Employee.get()

      rootScope.loading = true
      scope.child = Child.bind(school_id: stateParams.kindergarten, child_id: stateParams.child_id).get ->
        scope.refresh()

      scope.refresh = ->
        rootScope.loading = true
        scope.conversations = Message.bind(school_id: stateParams.kindergarten, topic: scope.child.child_id).query ->
          _.forEach scope.conversations, (c) ->
            c.sender.info = Sender.bind(school_id: stateParams.kindergarten, id: c.sender.id, type: c.sender.type).get ->
              c.sender.name = c.sender.info.name
          scope.message = scope.newHistoryRecord()
          if scope.conversations.length > 0
            r = new ReaderLog
              school_id: parseInt stateParams.kindergarten
              topic: scope.child.child_id
              reader: scope.adminUser.id
              session_id: scope.conversations[scope.conversations.length - 1].id
            r.$save()
          rootScope.loading = false

      scope.newHistoryRecord = ->
        new Message
          school_id: stateParams.kindergarten
          topic: scope.child.child_id
          content: ''
          medium: []
          timestamp: 0
          sender:
            id: scope.adminUser.id
            type: 't'
            name: scope.adminUser.name


      scope.preview = (msg, option) ->
        scope.viewOption = _.extend reply: true, option
        scope.viewing_message = msg

        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/view_history_record.html'


      scope.cancelEditing = (message) ->
        scope.message = scope.newHistoryRecord()

      scope.send = (msg) ->
        rootScope.loading = true
        msg.$save ->
          scope.refresh()

      scope.delete = (msg) ->
        rootScope.loading = true
        msg.$delete ->
          scope.refresh()

      urlOfToken = (token) ->
        if location.host() == 'localhost'
          "http://#{location.host()}:#{location.port()}/s/#{token}"
        else
          "http://#{location.host()}/s/#{token}"

      scope.share = (msg) ->
        sharingMessage = _.extend msg, school_id: stateParams.kindergarten
        Share.save sharingMessage, (data) ->
          url = urlOfToken data.token
          alert(url)
          scope.refresh()

      scope.messageEditing = (message) ->
        scope.disableUploading = false
        scope.message = angular.copy message
        scope.currentModal = Modal
          scope: scope
          keyboard: false
          contentTemplate: 'templates/admin/add_history_record.html'

      scope.messageDeleting = (message) ->
        rootScope.loading = true
        message.$delete ->
          scope.refresh()
          scope.currentModal.hide()

      scope.detectType = (url) ->
        if url && url.match /\.(jpg|png)$/i
          'image'
        else if url && url.match /\.(mp3|amr)$/i
          'audio'
        else if url && url.match /\.(mp4)$/i
          'video'
        else
          'unknown'

      scope.disableUploading = false
      scope.buttonLabel = '添加'
      scope.onUploadSuccess = (url) ->
        scope.$apply ->
          scope.message.medium.push url: url, type: scope.detectType(url) if url isnt undefined
          scope.disableUploading = scope.dynamicDisable(scope.message)
          scope.buttonLabel = scope.dynamicLabel(scope.message)

      scope.dynamicLabel = (message)->
        if message.medium.length == 0 then '添加' else '继续添加'

      scope.dynamicDisable = (message) ->
        message.medium && message.medium.length > 8

      scope.deletable = (message) ->
        (message.sender? && message.sender.id == scope.adminUser.id) || undefined isnt _.find ['principal', 'operator'], (u) -> scope.adminUser.privilege_group == u

      scope.deleteMedia = (media) ->
        _.pullAt scope.message.medium, _.findIndex scope.message.medium, 'url', media.url
        scope.disableUploading = scope.dynamicDisable(scope.message)
        scope.buttonLabel = scope.dynamicLabel(scope.message)

  ]