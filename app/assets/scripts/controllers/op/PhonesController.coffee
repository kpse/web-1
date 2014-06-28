angular.module('kulebaoOp').controller 'OpPhoneManagementCtrl',
  ['$scope', '$rootScope', '$location', '$stateParams', 'phoneManageService', '$alert', 'allEmployeesService',
    (scope, rootScope, location, stateParams, Phone, Alert, Employee) ->
      rootScope.tabName = 'phone_management'

      notFound = (phone) ->
        Alert
          title: '未找到手机号' + phone
          content: '请修改条件进行查找。'
          placement: "top-left"
          type: "danger"
          container: '.well'
          duration: 3

      scope.query = (phone) ->
        scope.parent = Phone.get phone: phone, ->
          scope.navigateTo(phone)
        , (res) ->
          scope.teacher = Employee.get phone: phone, ->
            scope.navigateTo(phone, 'teacher')
          , (res) ->
            notFound(phone)

      scope.navigateTo = (phone, type = 'phone')->
        location.path '/main/phone_management/' + type + '/' + phone

      scope.alertDelete = (person) ->
        type = if person.parent_id? then '家长' else '老师'
        Alert
          title: '删除成功'
          content: '手机号为' + person.phone + '的' + type + '已经删除。'
          placement: "top"
          type: "danger"
          container: '.well'
          duration: 3

      scope.cancel = ->
        location.path '/main/phone_management'
  ]

angular.module('kulebaoOp').controller 'OpShowPhoneCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'phoneManageService', 'schoolService', '$alert',
    (scope, rootScope, stateParams, location, Phone, School, Alert) ->
      scope.parent = Phone.get phone: stateParams.phone, ->
        scope.school = School.get school_id: scope.parent.school_id

      scope.delete = (parent)->
        Phone.delete parent, ->
          scope.alertDelete(parent)
          scope.cancel()
  ]

angular.module('kulebaoOp').controller 'OpShowTeacherCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'allEmployeesService', 'schoolService', '$alert',
    (scope, rootScope, stateParams, location, Employee, School, Alert) ->
      scope.teacher = Employee.get phone: stateParams.phone, ->
        scope.school = School.get school_id: scope.teacher.school_id

      scope.delete = (person)->
        Employee.delete person, ->
          scope.alertDelete(person)
          scope.cancel()
  ]

