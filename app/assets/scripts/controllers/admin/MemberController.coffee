'use strict'

angular.module('kulebaoAdmin')
.controller 'MembersListCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$location', 'chargeService', 'accessClassService',
    (scope, rootScope, stateParams, location, Charge, AccessClass) ->
      rootScope.tabName = 'member'

      scope.refresh = ->
        AccessClass(scope.kindergarten.classes)
        Charge.query school_id: stateParams.kindergarten, (data)->
          scope.kindergarten.charge = data[0]
          if scope.kindergarten.charge && scope.kindergarten.charge.status == 1
            scope.heading = '全校已开通( ' + scope.kindergarten.charge.used + ' / ' + scope.kindergarten.charge.total_phone_number + ' 人)'
          else
            scope.heading = '学校未开通手机幼乐宝服务，请与幼乐宝服务人员联系'

      scope.$on 'update_charge', ->
        scope.refresh()

      scope.refresh()

      scope.navigateTo = (c) ->
        location.path("kindergarten/#{stateParams.kindergarten}/member/class/#{c.class_id}/list")

  ]

.controller 'MembersInClassCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    'parentService', '$modal', 'chargeService',
    '$alert', 'relationshipService',
    (scope, rootScope, stateParams, Parent, Modal, Charge, Alert, Relationship) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.currentClass = ->
        _.find scope.kindergarten.classes, (c) ->
          c.class_id == scope.current_class

      generateDetail = (people) ->
        _.forEach people, (m) ->
          m.relationships = Relationship.bind(school_id: stateParams.kindergarten).query parent: m.phone, ->
            m.children = _.map m.relationships, (r) -> r.child.name

      scope.refresh = ->
        scope.loading = true
        scope.members = Parent.members school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
          generateDetail(scope.members)
          scope.nonMembers = Parent.nonMembers school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
            generateDetail(scope.nonMembers)
            scope.kindergarten.charge = Charge.query school_id: stateParams.kindergarten, ->
              scope.$emit 'update_charge'
              scope.loading = false
              scope.membersInClass = _.uniq scope.members, (m) -> m.parent_id
              scope.nonMembersInClass = _.uniq scope.nonMembers, (m) -> m.parent_id

      scope.refresh()

      scope.exceed = ->
        scope.kindergarten.charge.used >= scope.kindergarten.charge.total_phone_number

      scope.promote = (parent) ->
        return scope.noPromotion() if scope.exceed()
        parent.member_status = 1
        parent.$save school_id: stateParams.kindergarten, ->
          scope.refresh()

      scope.reject = (member) ->
        member.member_status = 0
        member.$save school_id: stateParams.kindergarten, ->
          scope.refresh()

      scope.noPromotion = ->
        Alert
          title: '无法开通'
          content: '目前开通人数已经达到上限，请联系幼乐宝管理员开通更多授权。'
          container: '.panel-body'

  ]