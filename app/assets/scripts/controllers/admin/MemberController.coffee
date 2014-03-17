'use strict'

angular.module('kulebaoAdmin')
.controller 'MembersListCtrl',
    [ '$scope', '$rootScope', '$stateParams', 'schoolService', 'classService', '$location',
      (scope, rootScope, stateParams, School, Class, location) ->
        rootScope.tabName = 'member'
        scope.heading = '已开通手机'

        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.classes = Class.bind({school_id: scope.kindergarten.school_id}).query ->
            location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

        scope.navigateTo = (c) ->
          location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

    ]

angular.module('kulebaoAdmin')
.controller 'MembersInClassCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', 'classService', 'parentService', '$modal', 'employeeService',
      (scope, rootScope, stateParams, location, School, Class, Parent, Modal, Employee) ->
        scope.loading = true
        scope.current_class = parseInt(stateParams.class_id)

        scope.adminUser = Employee.get ->
          scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
            scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
              scope.refresh()

        scope.refresh = ->
          scope.loading = true
          scope.members = Parent.members school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
            scope.nonMembers = Parent.nonMembers school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
              scope.loading = false

        scope.promote = (parent) ->
          parent.member_status = 1
          parent.$save school_id: stateParams.kindergarten, ->
            scope.refresh()

        scope.reject = (member) ->
          member.member_status = 0
          member.$save school_id: stateParams.kindergarten, ->
            scope.refresh()


    ]