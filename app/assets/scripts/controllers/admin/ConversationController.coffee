'use strict'

angular.module('kulebaoAdmin')
.controller 'ConversationsListCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      'schoolService', 'classService', '$location'
      (scope, rootScope, stateParams, School, Class, location) ->
        rootScope.tabName = 'conversation'
        scope.heading = '联系家长'

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
      '$location', 'schoolService', 'classService', 'parentService', 'conversationService', 'relationshipService'
      (scope, rootScope, stateParams, location, School, Class, Parent, Chat, Relationship) ->
        scope.loading = true
        scope.current_class = parseInt(stateParams.class_id)

        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query()
          scope.relationships = Relationship.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id).query ->
            _.forEach scope.relationships, (r) ->
              r.parent.messages = Chat.bind(school_id: stateParams.kindergarten, phone: r.parent.phone, most: 1).query ->
                r.parent.lastMessage = r.parent.messages[0]
            scope.loading = false


        scope.goDetail = (card) ->
          if (location.path().indexOf('/list') > 0 )
            location.path location.path().replace(/\/list$/, '/card/' + card)
          else
            location.path location.path().replace(/\/card\/\d+$/, '') + '/card/' + card


    ]

angular.module('kulebaoAdmin')
.controller 'ConversationCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', '$http', 'classService', 'conversationService', 'relationshipService', '$modal',
      '$popover', '$tooltip', 'employeeService'
      (scope, rootScope, stateParams, location, School, $http, Class, Message, Relationship, Modal, Popover, Tooltip,
       Employee) ->
        scope.adminUser = Employee.get()

        scope.loading = true
        scope.relationship = Relationship.bind(school_id: stateParams.kindergarten, card: stateParams.card).get ->
          scope.conversations = Message.bind(school_id: stateParams.kindergarten, phone: scope.relationship.parent.phone).query ->
            scope.loading = false
            scope.message = scope.newMessage()

        scope.newMessage = ->
          new Message
            school_id: stateParams.kindergarten
            phone: scope.relationship.parent.phone
            content: ''
            image: ''
            timestamp: 0
            sender: scope.adminUser.name

        scope.preview = (msg, option) ->
          rootScope.viewOption = _.extend reply: true, option
          rootScope.currentMessage = msg

          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/view_message.html'


        scope.send = (msg) ->
          return if msg.content is ''
          msg.$save ->
            scope.message = scope.newMessage()
            scope.conversations = Message.bind(school_id: stateParams.kindergarten, phone: scope.relationship.parent.phone).query()

        scope.messageEditing = (msg)->
          rootScope.editingMessage = msg

          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_message.html'
    ]

angular.module('kulebaoAdmin')
.controller 'AddMessageCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', '$http', 'classService', 'conversationService', 'relationshipService', 'uploadService'
      (scope, rootScope, stateParams, location, School, $http, Class, Message, Relationship, uploadService) ->
        if rootScope.editingMessage is undefined
          scope.$hide()
        else
          scope.message = rootScope.editingMessage
          delete rootScope.editingParent

        scope.conversations = Message.bind(school_id: stateParams.kindergarten, phone: scope.relationship.parent.phone, most: 5).query()
        scope.relationship = Relationship.bind(school_id: stateParams.kindergarten, card: stateParams.card).get()

        scope.uploadPic = (message, pic) ->
          upload pic, (url) ->
            scope.$apply ->
              message.image = url if url isnt undefined

        upload = (file, callback)->
          return callback(undefined) if file is undefined
          $http.get('/ws/fileToken?bucket=suoqin-test').success (data)->
            uploadService.send file, data.token, (remoteFile) ->
              callback(remoteFile.url)
    ]

angular.module('kulebaoAdmin')
.controller 'ViewMessageCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', '$http', 'classService', 'conversationService', 'relationshipService',
      (scope, rootScope, stateParams, location, School, $http, Class, Message, Relationship) ->
        if rootScope.currentMessage is undefined
          scope.$hide()
        else
          scope.message = rootScope.currentMessage
          scope.viewOption = rootScope.viewOption
          delete rootScope.currentMessage

        scope.conversations = Message.bind(school_id: stateParams.kindergarten, phone: scope.relationship.parent.phone, most: 5).query()
        scope.relationship = Relationship.bind(school_id: stateParams.kindergarten, card: stateParams.card).get()
    ]