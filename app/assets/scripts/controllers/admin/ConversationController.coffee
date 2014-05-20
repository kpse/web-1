'use strict'

angular.module('kulebaoAdmin')
.controller 'ConversationsListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    'schoolService', 'classService', '$location'
    (scope, rootScope, stateParams, School, Class, location) ->
      rootScope.tabName = 'conversation'
      scope.heading = '使用该功能与家长直接对话'

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
          scope.loading = false
          location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

      scope.navigateTo = (c) ->
        location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

  ]

angular.module('kulebaoAdmin')
.controller 'ConversationsInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', 'classService', 'parentService', 'chatSessionService', 'childService',
    'senderService', 'readRecordService', 'employeeService',
    (scope, rootScope, stateParams, location, School, Class, Parent, Chat, Child, Sender, ReaderLog, Employee) ->
      scope.loading = true
      scope.current_class = parseInt(stateParams.class_id)
      scope.adminUser = Employee.get()

      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query()
        scope.children = Child.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true).query ->
          _.forEach scope.children, (child) ->
            child.messages = Chat.bind(school_id: stateParams.kindergarten, topic: child.child_id, most: 1).query ->
              child.lastMessage = child.messages[0]
              if child.lastMessage isnt undefined
                child.lastMessage.sender.info = Sender.bind(school_id: stateParams.kindergarten, id: child.lastMessage.sender.id, type: child.lastMessage.sender.type).get ->
                  child.lastMessage.sender.name = child.lastMessage.sender.info.name
                  child.lastMessage.sender.read_record = ReaderLog.bind(school_id: stateParams.kindergarten, topic: child.child_id, reader: scope.adminUser.id).get ->
                    child.lastMessage.isRead = child.lastMessage.sender.read_record.session_id >= child.lastMessage.id

          scope.loading = false


      scope.goDetail = (child) ->
        if (location.path().indexOf('/list') > 0 )
          location.path location.path().replace(/\/list$/, '/child/' + child.child_id)
        else
          location.path location.path().replace(/\/card\/\d+$/, '') + '/child/' + child.child_id


  ]

angular.module('kulebaoAdmin')
.controller 'ConversationCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', '$http', 'classService', 'chatSessionService', 'childService', '$modal',
    '$popover', '$tooltip', 'employeeService', 'uploadService', 'senderService',
    (scope, rootScope, stateParams, location, School, $http, Class, Message, Child, Modal, Popover, Tooltip, Employee, Upload, Sender) ->
      scope.adminUser = Employee.get()

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

      scope.uploadPic = (message, pic) ->
        scope.uploading = true
        Upload pic, (url) ->
          scope.$apply ->
            message.media.url = url if url isnt undefined
            scope.uploading = false
        , scope.adminUser.id
  ]