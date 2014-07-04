'use strict'

angular.module('kulebaoAdmin')
.controller 'HistoryListCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    'schoolService', 'classService', '$location', 'imageCompressService', 'accessClassService',
    (scope, rootScope, stateParams, School, Class, location, Compress, AccessClass) ->
      rootScope.tabName = 'history'
      scope.heading = '记录小朋友成长的各种瞬间'

      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
          AccessClass(scope.kindergarten.classes)

      scope.navigateTo = (c) ->
        location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

      scope.compress = (url, width, height) ->
        Compress.compress(url, width, height)

      scope.isImage = (media) ->
        media && /.(jpg|png)$/.test(media.url)
      scope.isAudio = (media) ->
        media && !scope.isImage(media)
  ]

angular.module('kulebaoAdmin')
.controller 'HistoryInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', 'classService', 'parentService', 'historyService', 'childService',
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
              child.lastMessage.isRead = true if child.lastMessage isnt undefined
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
          location.path location.path().replace(/\/child\/\d+$/, '') + '/child/' + child.child_id


  ]

angular.module('kulebaoAdmin')
.controller 'HistoryCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$location', 'schoolService', '$http', 'classService', 'historyService', 'childService', '$modal',
    '$popover', '$tooltip', 'employeeService', 'uploadService', 'senderService', 'readRecordService',
    (scope, rootScope, stateParams, location, School, $http, Class, Message, Child, Modal, Popover, Tooltip, Employee, Upload, Sender, ReaderLog) ->
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
          scope.message = scope.newHistoryRecord()
          if scope.conversations.length > 0
            r = new ReaderLog
              school_id: parseInt stateParams.kindergarten
              topic: scope.child.child_id
              reader: scope.adminUser.id
              session_id: scope.conversations[scope.conversations.length - 1].id
            r.$save()
          scope.loading = false

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


      scope.send = (msg) ->
          msg.$save ->
            scope.refresh()

      scope.messageEditing = ->
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_history_record.html'

      scope.messageDeleting = (message) ->
        message.$delete ->
          scope.refresh()
          scope.currentModal.hide()

      scope.uploadPic = (message, thatScope) ->
        scope.uploading = true
        Upload thatScope.pic, (url) ->
          scope.$apply ->
            message.medium.push url : url, type: 'image' if url isnt undefined
            scope.uploading = false
            thatScope.pic = undefined
        , scope.adminUser.id
  ]