angular.module('kulebaoOp').controller 'OpSchoolCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', '$modal', 'principalService', 'allEmployeesService',
   '$resource', 'chargeService', 'adminCreatingService', '$alert', '$location',
    (scope, rootScope, School, Clazz, Modal, Principal, Employee, $resource, Charge, AdminCreating, Alert, location) ->
      scope.refresh = ->
        scope.kindergartens = School.query ->
          _.each scope.kindergartens, (kg) ->
            kg.managers = Principal.query school_id: kg.school_id, ->
              _.map kg.managers, (p) ->
                p.detail = Employee.get phone: p.phone
            kg.charge = Charge.query school_id: kg.school_id
          scope.admins = Employee.query()


      scope.refresh()

      rootScope.tabName = 'school'

      scope.editSchool = (kg) ->
        kg.charges = Charge.query school_id: kg.school_id, ->
          kg.charge = kg.charges[0]
          kg.principal =
            admin_login: kg.managers[0].detail.login_name if kg.managers[0]?

          scope.school = angular.copy kg
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/op/edit_school.html'

      scope.goChargePage = ->
        location.path '/main/charge'

      scope.endEditing = (kg) ->
        School.save kg, ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError res

      scope.edit = (user) ->
        scope.employee = angular.copy user
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
          phone: '12121311131'
          name: '短名字'
          token: '1'
          address: '四川省某个地区'
          full_name: '新学校全名'
          principal:
            admin_login: 'admin' + id
            admin_password: '',
          charge:
            school_id: id
            expire_date: '2015-01-01'
            total_phone_number: 0
            status: 1
            used: 0

      scope.idChange = (school)->
        school.principal.admin_login = 'admin' + school.school_id
        school.charge.school_id = school.school_id

      scope.addSchool = ->
        scope.school = scope.newSchool()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_school.html'

      handleError = (res) ->
        Alert
          title: '无法保存学校信息'
          content: res.data.error_msg
          placement: "top-left"
          type: "danger"
          show: true
          container: '.panel-body'
          duration: 3

      scope.saveSchool = (school) ->
        AdminCreating.save school, ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError res

      scope.save = (object) ->
        if object.group
          Manager = $resource('/kindergarten/' + object.school_id + '/principal')
          Manager.save object, ->
            scope.refresh()
            scope.currentModal.hide()
        else
          object.$save ->
            scope.refresh()
            scope.currentModal.hide()

      scope.isSchoolDuplicated = (school, field) ->
        return false if school[field] is undefined
        undefined isnt _.find scope.kindergartens, (k) ->
          k[field] == school[field] && k.school_id isnt k.school_id

      nextId = (schools)->
        13 + _.max _.map schools, (c) ->
          c.school_id

  ]

