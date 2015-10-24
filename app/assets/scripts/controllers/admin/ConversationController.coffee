'use strict'

angular.module('kulebaoAdmin')
.controller 'ConversationsListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    'imageCompressService', 'accessClassService',
    (scope, rootScope, stateParams, Compress, AccessClass) ->
      rootScope.tabName = 'conversation'
      scope.heading = '使用该功能与家长直接对话'

      AccessClass(scope.kindergarten.classes)

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

  ]

.controller 'ConversationsInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$timeout',
    '$state', 'chatSessionService', 'childService',
    'senderService', 'readRecordService',
    (scope, rootScope, stateParams, $timeout, $state, Chat, Child, Sender, ReaderLog) ->
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

      scope.goDetail = (child) ->
        rootScope.loading = true
        $timeout ->
          $state.go 'kindergarten.conversation.class.child', {kindergarten: stateParams.kindergarten, class_id: child.class_id, child_id: child.child_id}

      scope.navigateTo = (c) ->
        if c.class_id != scope.current_class
          rootScope.loading = true
          $timeout ->
            $state.go 'kindergarten.conversation.class.list', {kindergarten: stateParams.kindergarten, class_id: c.class_id}

  ]
.controller 'ConversationCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', '$http', 'chatSessionService', 'childService', '$modal',
    '$popover', '$tooltip', 'senderService', 'readRecordService',
    (scope, rootScope, stateParams, location, $http, Message, Child, Modal, Popover, Tooltip, Sender, ReaderLog) ->

      rootScope.loading = true
      scope.noMore = false
      scope.child = Child.bind(school_id: stateParams.kindergarten, child_id: stateParams.child_id).get ->
        scope.refresh()

      scope.refresh = (most=25) ->
        rootScope.loading = true
        scope.conversations = Message.bind(school_id: stateParams.kindergarten, topic: scope.child.child_id, most: most).query ->
          _.forEach scope.conversations, (c) ->
            c.sender.info = Sender.bind(school_id: stateParams.kindergarten, id: c.sender.id, type: c.sender.type).get ->
              c.sender.name = c.sender.info.name
          scope.message = scope.newMessage()
          if scope.conversations.length > 0
            r = new ReaderLog
              school_id: parseInt stateParams.kindergarten
              topic: scope.child.child_id
              reader: scope.adminUser.id
              session_id: scope.conversations[scope.conversations.length - 1].id
            r.$save ->
              scope.$emit 'sessionRead'
          scope.noMore = scope.conversations.length < most
          rootScope.loading = false

      scope.newMessage = ->
        new Message
          school_id: stateParams.kindergarten
          topic: scope.child.child_id
          content: ''
          media:
            url: ''
            type: 'image'
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
          contentTemplate: 'templates/admin/view_message.html'


      scope.send = (msg) ->
        if msg.media.url
          imageMsg = angular.copy msg
          imageMsg.content = ''
          msg.media.url = ''
          imageMsg.$save ->
            if msg.content
              msg.$save ->
                scope.refresh()
            else
              scope.refresh()
        else
          msg.$save ->
            scope.refresh()

      scope.messageEditing = ->
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_message.html'

      scope.loadMore = (current) ->
        scope.refresh(current.length + 25)

  ]