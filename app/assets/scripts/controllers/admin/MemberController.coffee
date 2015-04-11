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
        _.forEach people, (m) ->
          m.relationships = Relationship.bind(school_id: stateParams.kindergarten).query parent: m.phone, ->
            m.children = _.map m.relationships, (r) -> r.child.name

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
        scope.loading = true
        initData()
        scope.members = Parent.members school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
          generateDetail(scope.members)
          scope.nonMembers = Parent.nonMembers school_id: stateParams.kindergarten, class_id: stateParams.class_id, ->
            generateDetail(scope.nonMembers)
            scope.kindergarten.charge = Charge.query school_id: stateParams.kindergarten, ->
              scope.$emit 'update_charge'
              scope.membersInClass = _.uniq scope.members, (m) -> m.parent_id
              scope.nonMembersInClass = _.uniq scope.nonMembers, (m) -> m.parent_id
              scope.loading = false


      scope.refresh()

      scope.exceed = ->
        scope.kindergarten.charge.used >= scope.kindergarten.charge.total_phone_number

      scope.promote = (parent) ->
        if scope.exceed()
          deferred = $q.defer();
          $timeout ->
              scope.noPromotion()
            , 100
          deferred.promise
        else
          parent.member_status = 1
          parent.$save(school_id: stateParams.kindergarten).$promise


      scope.reject = (member) ->
        member.member_status = 0
        member.$save(school_id: stateParams.kindergarten).$promise

      scope.noPromotion = ->
        Alert
          title: '无法开通'
          content: '目前开通人数已经达到上限，请联系幼乐宝管理员开通更多授权。'
          container: '.panel-body'

      scope.checkAllMembers = (check) ->
        _.forEach scope.membersInClass, (r) ->
          r.checked = check

      scope.checkAllNonMembers = (check) ->
        _.forEach scope.nonMembersInClass, (r) ->
          r.checked = check

      scope.multipleSuspend = ->
        checked = _.filter scope.membersInClass, (member) ->
          member.checked? && member.checked == true
        queue = _.map checked, (member) ->
          scope.reject member
        all = $q.all queue
        all.then (q) ->
          scope.loading = true
          $timeout ->
              scope.refresh()
            , 500

      scope.multiplePrompt = ->
        checked = _.filter scope.nonMembersInClass, (nonMember) ->
          nonMember.checked? && nonMember.checked == true
        queue = _.map checked, (parent) ->
          scope.promote parent
        all = $q.all queue
        all.then (q) ->
          scope.loading = true
          $timeout ->
              scope.refresh()
            , 500

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