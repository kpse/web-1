'use strict'

angular.module('kulebaoAdmin')
.controller 'AssessListCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      'schoolService', 'classService', '$location'
      (scope, rootScope, stateParams, School, Class, location) ->
        rootScope.tabName = 'baby-status'
        scope.heading = '评价宝宝最近的表现'

        scope.loading = true
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
            scope.loading = false
            location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

        scope.navigateTo = (c) ->
          location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

    ]

angular.module('kulebaoAdmin')
.controller 'AssessInClassCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', 'classService', 'assessService', 'childService',
      (scope, rootScope, stateParams, location, School, Class, Assess, Child) ->
        scope.loading = true
        scope.current_class = parseInt(stateParams.class_id)

        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query()
          scope.children = Child.bind(school_id: stateParams.kindergarten, class_id: stateParams.class_id, connected: true).query ->
            _.forEach scope.children, (c) ->
              c.status = Assess.bind(school_id: stateParams.kindergarten, child_id: c.id, most: 1).query ->
                c.recentStatus = c.status[0]
            scope.loading = false

        scope.goDetail = (child) ->
          if (location.path().indexOf('/list') > 0 )
            location.path location.path().replace(/\/list$/, '/child/' + child.id)
          else
            location.path location.path().replace(/\/child\/\d+$/, '') + '/child/' + child.id


    ]

angular.module('kulebaoAdmin')
.controller 'AssessCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', '$http', 'classService', 'assessService', 'childService', '$modal',
      '$popover', '$tooltip', 'employeeService'
      (scope, rootScope, stateParams, location, School, $http, Class, Assess, Child, Modal, Popover, Tooltip, Employee) ->
        scope.adminUser = Employee.get()

        scope.loading = true
        scope.child = Child.bind(school_id: stateParams.kindergarten, child_id: stateParams.child).get ->
          scope.refreshAssess()


        scope.$on 'refreshAssess', ->
          scope.refreshAssess()

        scope.refreshAssess = ->
          scope.loading = true
          scope.allAssess = Assess.bind(school_id: stateParams.kindergarten, child_id: stateParams.child).query ->
            scope.loading = false
            scope.message = scope.newMessage()

        scope.newMessage = ->
          new Assess
            school_id: stateParams.kindergarten
            child_id: scope.child.child_id
            publisher: scope.adminUser.name
            emotion: 3
            activity: 3
            rest: 3
            game: 3
            self_care: 3
            exercise: 3
            manner: 3
            dining: 3
            comments: ''

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

        scope.create = ->
          rootScope.editingAssess = new Assess
            child_id: stateParams.child
            school_id: parseInt stateParams.kindergarten
            publisher: scope.adminUser.name
            emotion: 3
            activity: 3
            rest: 3
            game: 3
            self_care: 3
            exercise: 3
            manner: 3
            dining: 3
            comments: ''

          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_assess.html'
    ]

angular.module('kulebaoAdmin')
.controller 'NewAssessCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', '$http', 'classService', 'childService', 'assessService', 'uploadService'
      (scope, rootScope, stateParams, location, School, $http, Class, Child, Assess, uploadService) ->
        if rootScope.editingAssess is undefined
          scope.$hide()
        else
          scope.assess = rootScope.editingAssess
          delete rootScope.editingAssess

        scope.save = (assess)->
          assess.$save ->
            scope.$emit 'refreshAssess'
            scope.$hide()
    ]

angular.module('kulebaoAdmin')
.controller 'ViewAssessCtrl',
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