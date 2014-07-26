'use strict'

angular.module('kulebaoAdmin')
.controller 'ConversationsListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    'classService', '$location', 'imageCompressService', 'accessClassService',
    (scope, rootScope, stateParams, Class, location, Compress, AccessClass) ->
      rootScope.tabName = 'conversation'
      scope.heading = '使用该功能与家长直接对话'

      scope.loading = true
      AccessClass(scope.kindergarten.classes)

      scope.navigateTo = (c) ->
        location.path("kindergarten/#{stateParams.kindergarten}/conversation/class/#{c.class_id}/list")

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

  ]

.controller 'ConversationsInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'classService', 'parentService', 'chatSessionService', 'childService',
    'senderService', 'readRecordService',
    (scope, rootScope, stateParams, location, Class, Parent, Chat, Child, Sender, ReaderLog) ->
      scope.loading = true
      scope.current_class = parseInt(stateParams.class_id)

      scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query()
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

        scope.loading = false


      scope.goDetail = (child) ->
        location.path "kindergarten/#{stateParams.kindergarten}/conversation/class/#{child.class_id}/child/#{child.child_id}"

  ]
.controller 'ConversationCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', '$http', 'classService', 'chatSessionService', 'childService', '$modal',
    '$popover', '$tooltip', 'senderService', 'readRecordService',
    (scope, rootScope, stateParams, location, $http, Class, Message, Child, Modal, Popover, Tooltip, Sender, ReaderLog) ->

      scope.loading = true
      scope.child = Child.bind(school_id: stateParams.kindergarten, child_id: stateParams.child_id).get ->
        scope.refresh()

      scope.refresh = ->
        scope.loading = true
        scope.conversations = Message.bind(school_id: stateParams.kindergarten, topic: scope.child.child_id).query ->
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
            r.$save()
          scope.loading = false

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
  ]