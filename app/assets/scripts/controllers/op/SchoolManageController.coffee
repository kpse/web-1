angular.module('kulebaoOp').controller 'OpSchoolCtrl',
  ['$scope', '$rootScope', '$filter', 'schoolService', 'classService', '$modal', 'principalService', 'allEmployeesService',
   '$resource', 'chargeService', 'adminCreatingService', '$alert', '$location', 'schoolConfigService',
   'schoolConfigExtractService',
    (scope, rootScope, $filter, School, Clazz, Modal, Principal, Employee, $resource, Charge, AdminCreating, Alert, location, SchoolConfig, ConfigExtract) ->
      scope.refresh = ->
        extractConfig = ConfigExtract

        scope.generateConfigArray = (all) ->
          _.map all, (value, key) ->
            {name: key, value: value}

        scope.defaultConfig =
          backend: 'true'
          hideVideo: 'false'
          disableMemberEditing: 'false'
          bus: 'false'

        scope.filterConfig = (config, index) ->
          scope.defaultConfig[config.name] != config.value

        scope.kindergartens = School.query ->
          _.each scope.kindergartens, (kg) ->
            kg.managers = Principal.query school_id: kg.school_id, ->
              _.map kg.managers, (p) ->
                p.detail = Employee.get phone: p.phone
            Charge.query school_id: kg.school_id, (data) ->
              kg.charge = data[0]
              kg.charge.expire = new Date(data[0].expire_date)


            SchoolConfig.get school_id: kg.school_id, (data)->
              kg.config =
                backend: extractConfig data['config'], 'backend', scope.defaultConfig['backend']
                hideVideo: extractConfig data['config'], 'hideVideo', scope.defaultConfig['hideVideo']
                disableMemberEditing: extractConfig data['config'], 'disableMemberEditing', scope.defaultConfig['disableMemberEditing']
                bus: extractConfig data['config'], 'bus', scope.defaultConfig['bus']
              kg.configArray = scope.generateConfigArray(kg.config)

          scope.admins = Employee.query()


      scope.refresh()

      rootScope.tabName = 'school'

      scope.editSchool = (kg) ->
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
        kg.properties = scope.generateConfigArray kg.config
        School.save kg, ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError res

      scope.edit = (user) ->
        scope.employee = angular.copy user.detail
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.createPrincipal = (school) ->
        new Employee
          school_id: school
          birthday: '1980-01-01'
          gender: 0
          login_password: ''
          login_name: ''
          workgroup: ''
          workduty: ''
          phone: ''
          group: 'principal'

      scope.addManager = (kg) ->
        scope.employee = scope.createPrincipal(kg.school_id)
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'


      scope.delete = (kg)->
        kg.$delete ->
          scope.refresh()


      scope.newSchool = ->
        id = nextId(scope.kindergartens)
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

      scope.addSchool = ->
        scope.school = scope.newSchool()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_school.html'

      handleError = (res) ->
        Alert
          title: '无法保存学校信息'
          content: if res.data.error_msg? then res.data.error_msg else res.data
          placement: "top"
          type: "danger"
          show: true
          container: '.modal-dialog .panel-body'
          duration: 3

      scope.saveSchool = (school) ->
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
  ]

