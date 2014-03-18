'use strict'

angular.module('kulebaoAdmin')
.controller 'MembersListCtrl',
    [ '$scope', '$rootScope', '$stateParams', 'schoolService', 'classService', '$location', 'chargeService',
      (scope, rootScope, stateParams, School, Class, location, Charge) ->
        rootScope.tabName = 'member'

        scope.refresh = ->
          scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
            scope.kindergarten.classes = Class.query school_id: scope.kindergarten.school_id, ->
              scope.kindergarten.charge = Charge.query school_id: stateParams.kindergarten, ->
                scope.heading = '全校已开通( '+ scope.kindergarten.charge[0].used + ' / ' +  scope.kindergarten.charge[0].total_phone_number + ' 人)' if scope.kindergarten.charge[0]
                location.path(location.path() + '/class/' + scope.kindergarten.classes[0].class_id + '/list') if (location.path().indexOf('/class/') < 0)

        scope.$on 'update_charge', ->
          scope.refresh()

        scope.refresh()

        scope.navigateTo = (c) ->
          location.path(location.path().replace(/\/class\/.+$/, '') + '/class/' + c.class_id + '/list')

    ]

angular.module('kulebaoAdmin')
.controller 'MembersInClassCtrl',
    [ '$scope', '$rootScope', '$stateParams',
      '$location', 'schoolService', 'classService', 'parentService', '$modal', 'employeeService', 'chargeService',
      (scope, rootScope, stateParams, location, School, Class, Parent, Modal, Employee, Charge) ->
        scope.loading = true
        scope.current_class = parseInt(stateParams.class_id)

        scope.adminUser = Employee.get ->
          scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
            scope.kindergarten.classes = Class.query school_id: stateParams.kindergarten, ->
              scope.refresh()

        scope.currentClass = ->
          _.find scope.kindergarten.classes, (c) -> c.class_id == scope.current_class

        scope.refresh = ->
          scope.loading = true
          scope.members = Parent.members school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
            scope.nonMembers = Parent.nonMembers school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
              scope.$emit 'update_charge'
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