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
  [ '$scope', '$rootScope', '$stateParams', '$q', '$timeout',
    'parentService', '$modal', 'chargeService',
    '$alert', 'relationshipService',
    (scope, rootScope, stateParams, $q, $timeout, Parent, Modal, Charge, Alert, Relationship) ->
      scope.current_class = parseInt(stateParams.class_id)

      scope.currentClass = ->
        _.find scope.kindergarten.classes, (c) ->
          c.class_id == scope.current_class

      generateDetail = (people) ->
        _.map people, (m) ->
          m.relationships = Relationship.bind(school_id: stateParams.kindergarten).query parent: m.phone, ->
            m.children = _.map m.relationships, (r) -> r.child.name
          m

      scope.selection =
        allMemberCheck: false
        allNonMemberCheck: false
      initData = ->
        scope.membersInClass = []
        scope.nonMembersInClass = []
        scope.members = []
        scope.nonMembers = []
        scope.selection.allMemberCheck = false
        scope.selection.allNonMemberCheck = false

      scope.refresh = ->
        rootScope.loading = true
        initData()
        Parent.members school_id: stateParams.kindergarten, class_id: stateParams.class_id, (data) ->
          scope.members = generateDetail(data)
          Parent.nonMembers school_id: stateParams.kindergarten, class_id: stateParams.class_id, (nonMemberData) ->
            scope.nonMembers = generateDetail(nonMemberData)
            scope.kindergarten.charge = Charge.query school_id: stateParams.kindergarten, ->
              scope.$emit 'update_charge'
              scope.membersInClass = _.uniq scope.members, (m) -> m.parent_id
              scope.nonMembersInClass = _.uniq scope.nonMembers, (m) -> m.parent_id
              rootScope.loading = false


      scope.refresh()

      scope.exceed = -> false

      scope.promote = (parent) ->
        parent.member_status = 1
        parent.$save(school_id: stateParams.kindergarten).$promise


      scope.reject = (member) ->
        member.member_status = 0
        member.$save(school_id: stateParams.kindergarten).$promise

      scope.checkAllMembers = (check) ->
        scope.membersInClass = _.map scope.membersInClass, (r) ->
          r.checked = check
          r

      scope.checkAllNonMembers = (check) ->
        scope.nonMembersInClass = _.map scope.nonMembersInClass, (r) ->
          r.checked = check
          r

      scope.multipleSuspend = ->
        queue = _(scope.membersInClass).filter((member) ->
          member.checked? && member.checked == true).uniq((m) -> m.parent_id).map((member) -> scope.reject member).value()
        all = $q.all queue
        all.then (q) ->
          rootScope.loading = true
          $timeout ->
              scope.refresh()
            , q.length * 50 + 200

      scope.multiplePrompt = ->
        queue = _(scope.nonMembersInClass).filter((nonMember) ->
          nonMember.checked? && nonMember.checked == true).uniq((m) -> m.parent_id).map((parent) -> scope.promote parent).value()
        all = $q.all queue
        all.then (q) ->
          rootScope.loading = true
          $timeout ->
              scope.refresh()
            , q.length * 50 + 200

      scope.hasSelection = (members) ->
        _.some members, (r) ->
          r.checked? && r.checked == true

      scope.toggle = (m) ->
        if m.checked?
          m.checked = !m.checked
        else
          m.checked = true

      scope.singleSelection = (m, members) ->
        allChecked = _.every members, (r) ->
          r.checked? && r.checked == true
        if m.member_status == 1
          scope.selection.allMemberCheck = allChecked
        else
          scope.selection.allNonMemberCheck = allChecked
  ]