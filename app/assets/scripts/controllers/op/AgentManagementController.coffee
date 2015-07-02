angular.module('kulebaoOp').controller 'OpAgentManagementCtrl',
  ['$scope', '$rootScope', '$filter', 'agentManagementService', '$modal', 'principalService', 'allEmployeesService',
   '$resource', 'chargeService', 'adminCreatingService', '$alert', '$location', 'agentSchoolService',
    (scope, rootScope, $filter, Agent, Modal, Principal, Employee, $resource, Charge, AdminCreating, Alert, location, School) ->
      rootScope.tabName = 'agent'

      scope.refresh = ->
        scope.agents = Agent.query ->
          _.forEach scope.agents, (a) ->
            a.schools = School.query a

      scope.refresh()



      scope.editAgent = (kg) ->
        scope.advancedEdting = false
        kg.charges = Charge.query school_id: kg.school_id, ->
          kg.charge = kg.charges[0]
          kg.principal =
            admin_login: kg.managers[0].detail.login_name if kg.managers && kg.managers[0]?

          scope.school = angular.copy kg
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/op/edit_school.html'

      scope.goChargePage = ->
        location.path '/main/charge'

      scope.endEditing = (kg) ->
        Agent.save kg, ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError res

      scope.edit = (user) ->
        scope.employee = angular.copy user.detail
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.deleteAgent = (agent)->
        agent.$delete ->
          scope.refresh()


      scope.newAgent = ->
        id = nextId(scope.agents)
        new School
          school_id: id
          address: ''
          principal:
            admin_login: "admin#{id}"
            admin_password: '',
          charge:
            school_id: id
            expire: new Date("#{new Date().getFullYear() + 1}-01-01")
            expire_date: "#{new Date().getFullYear() + 1}-01-01"
            total_phone_number: 0
            status: 1
            used: 0

      scope.idChange = (school)->
        school.principal.admin_login = "admin#{school.school_id}"
        school.charge.school_id = school.school_id

      scope.addAgent = ->
        scope.school = scope.newSchool()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_school.html'

      handleError = (res) ->
        Alert
          title: '无法保存代理商信息'
          content: if res.data.error_msg? then res.data.error_msg else res.data
          placement: "top"
          type: "danger"
          show: true
          container: '.modal-dialog .panel-body'
          duration: 3

      scope.saveAgent = (school) ->
        school.charge.expire_date = $filter('date')(school.charge.expire, 'yyyy-MM-dd', '+0800')
        school.phone = school.principal.phone unless school.phone? && school.phone.length > 0
        AdminCreating.save school, ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError res

      scope.save = (object) ->
        if object.group?
          Manager = $resource("/kindergarten/#{object.school_id}/principal")
          Manager.save object, ->
            scope.refresh()
            scope.currentModal.hide()
        else
          object.$save ->
            scope.refresh()
            scope.currentModal.hide()

      scope.isSchoolDuplicated = (school, field, form) ->
        return if !school[field]? or !form?
        target = _.find scope.kindergartens, (k) ->
          k[field] == school[field] && school.school_id isnt k.school_id
        if target?
          form.$setValidity 'unique', false
        else
          form.$setValidity 'unique', true

      scope.isSchoolIdDuplicated = (id, form) ->
        return unless form?
        target = _.find scope.kindergartens, (k) -> id == k.school_id
        if target?
          form.$setValidity 'unique', false
        else
          form.$setValidity 'unique', true

      nextId = (schools)->
        13 + _.max _.map schools, (c) ->
          c.school_id

      scope.displayIcon = (type) ->
        switch type
          when 'backend' then 'glyphicon-tower'
          when 'hideVideo' then 'glyphicon-facetime-video'
          when 'disableMemberEditing' then 'glyphicon-ban-circle'
          when 'bus' then 'glyphicon-record'

      scope.titleOf = (config) ->
          switch config.name
            when 'backend' then '无安全系统'
            when 'hideVideo' then '不显示视频图标'
            when 'disableMemberEditing' then '不允许学校开通或关闭会员'
            when 'bus' then '激活校车定位功能'

      scope.advancedEdting = false
      scope.advanced = ->
        scope.advancedEdting = true
      scope.simpleDialog = ->
        scope.advancedEdting = false

  ]

