angular.module('kulebaoOp').controller 'OpSchoolCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', '$modal', 'principalService', 'allEmployeesService', '$resource',
    (scope, rootScope, School, Clazz, Modal, Principal, Employee, $resource) ->
      scope.refresh = ->
        scope.kindergartens = School.query ->
          _.each scope.kindergartens, (kg) ->
            kg.managers = Principal.query school_id: kg.school_id, ->
               _.map kg.managers, (p) ->
                  p.detail = Employee.get phone: p.phone
          scope.admins = Employee.query()

      scope.refresh()

      rootScope.tabName = 'school'

      scope.editSchool = (kg) ->
        scope.school = angular.copy kg
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_school.html'

      scope.edit = (employee) ->
        scope.employee = angular.copy employee
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
        scope.employees = Employee.query ->
          scope.employee = scope.createPrincipal(kg.school_id)
          scope.currentModal = Modal
            scope: scope
            contentTemplate: 'templates/admin/add_employee.html'



      scope.delete = ->
        alert('暂未实现')

      scope.newSchool = ->
        new School
          school_id: nextId(scope.kindergartens)

      scope.addSchool = ->
        scope.school = scope.newSchool()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_school.html'

      scope.save = (object) ->
        if object.group
          Manager = $resource('/kindergarten/'+object.school_id+'/principal')
          m = new Manager
          m.$save object, ->
            scope.refresh()
            scope.currentModal.hide()
        else
          object.$save ->
            scope.refresh()
            scope.currentModal.hide()

      scope.isSchoolDuplicated = (school, field) ->
        return false if school[field] is undefined
        undefined isnt _.find scope.kindergartens, (k) ->
          k[field] == school[field]

      nextId = (schools)->
        13 + _.max _.map schools, (c) ->
          c.school_id

      scope.isDuplicated = (employee) ->
        return false if employee.phone is undefined || employee.phone.length < 10
        undefined isnt _.find scope.employees, (e) ->
          e.phone == employee.phone && e.id != employee.id

      scope.uploadPic = (employee, pic) ->
        Upload pic, (url) ->
          scope.$apply ->
            employee.portrait = url if url isnt undefined


  ]

