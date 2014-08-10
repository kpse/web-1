angular.module('kulebaoOp').controller 'OpPhoneManagementCtrl',
  ['$scope', '$rootScope', '$location', '$stateParams', 'phoneManageService', '$alert', 'allEmployeesService',
    (scope, rootScope, location, stateParams, Phone, Alert, Employee) ->
      rootScope.tabName = 'phone_management'

      notFound = (phone) ->
        Alert
          title: "未找到手机号#{phone}"
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
        location.path "/main/phone_management/#{type}/#{phone}"

      scope.alertDelete = (person) ->
        type = if person.parent_id? then '家长' else '老师'
        Alert
          title: '删除成功'
          content: "手机号为#{person.phone}的#{type}已经删除。"
          placement: "top"
          type: "danger"
          container: '.well'
          duration: 3

      scope.cancel = ->
        location.path '/main/phone_management'
  ]

.controller 'OpShowPhoneCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'phoneManageService', 'schoolService', 'videoMemberService',
    (scope, rootScope, stateParams, location, Phone, School, VideoMember) ->
      scope.parent = Phone.get phone: stateParams.phone, ->
        scope.school = School.get school_id: scope.parent.school_id
        scope.parent.videoMember = VideoMember.get school_id: scope.parent.school_id, id: scope.parent.parent_id

      scope.delete = (parent)->
        Phone.delete parent, ->
          scope.alertDelete(parent)
          scope.cancel()
  ]

.controller 'OpShowTeacherCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'allEmployeesService', 'schoolService',
    (scope, rootScope, stateParams, location, Employee, School) ->
      scope.teacher = Employee.get phone: stateParams.phone, ->
        if scope.teacher.school_id > 0
          scope.school = School.get school_id: scope.teacher.school_id
        else
          scope.school =
            name: '总管理员'

      scope.delete = (person)->
        Employee.delete person, ->
          scope.alertDelete(person)
          scope.cancel()
  ]

