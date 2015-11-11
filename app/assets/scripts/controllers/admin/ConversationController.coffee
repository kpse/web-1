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

      scope.$on 'clearSearch', ->
        scope.searchText = ''
  ]

.controller 'ConversationsClassSelectionCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$timeout',
    '$state', 'chatSessionService', 'childService',
    'senderService', 'readRecordService',
    (scope, rootScope, stateParams, $timeout, $state, Chat, Child, Sender, ReaderLog) ->
      rootScope.loading = true
      scope.current_class = parseInt(stateParams.class_id)

      scope.children = Child.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true).query ->
        _.each scope.children, (child) ->
          Chat.bind(school_id: stateParams.kindergarten, topic: child.child_id, most: 1).query (messages) ->
            child.lastMessage = _.first messages
            unless child.lastMessage?
              child.lastMessage =
                isRead: true
            else
              sender = child.lastMessage.sender
              sender.info = Sender.bind(school_id: stateParams.kindergarten, id: sender.id, type: sender.type).get ->
                sender.name = sender.info.name
                sender.read_record = ReaderLog.bind(school_id: stateParams.kindergarten, topic: child.child_id, reader: scope.adminUser.id).get ->
                  child.lastMessage.isRead = sender.read_record.session_id >= child.lastMessage.id

      scope.navigateTo = (c) ->
        if c.class_id != scope.current_class || !$state.is 'kindergarten.conversation.class.list'
          rootScope.loading = true
          $timeout ->
            $state.go 'kindergarten.conversation.class.list', {kindergarten: stateParams.kindergarten, class_id: c.class_id}
  ]
.controller 'ConversationsInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$timeout', '$state',
    (scope, rootScope, stateParams, $timeout, $state) ->
      rootScope.loading = true

      scope.goDetail = (child) ->
        scope.$emit 'clearSearch'
        rootScope.loading = true
        $timeout ->
          $state.go 'kindergarten.conversation.class.child', {kindergarten: stateParams.kindergarten, class_id: child.class_id, child_id: child.child_id}

      rootScope.loading = false
  ]
.controller 'ConversationCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$state', '$http', 'chatSessionService', 'childService', '$modal',
    '$popover', '$tooltip', 'senderService', 'readRecordService',
    (scope, rootScope, stateParams, $state, $http, Message, Child, Modal, Popover, Tooltip, Sender, ReaderLog) ->

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

      scope.cancelEditing = (message) ->
        scope.message = scope.newMessage()

      scope.messageEditing = ->
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_message.html'

      scope.loadMore = (current) ->
        scope.refresh(current.length + 25)

  ]