angular.module('kulebaoOp').controller 'OpPhoneManagementCtrl',
  ['$scope', '$rootScope', '$location', '$stateParams', 'phoneManageService', '$alert', 'allEmployeesService', 'relationshipSearchService',
    (scope, rootScope, location, stateParams, Phone, Alert, Employee, Relationship) ->
      rootScope.tabName = 'phone_management'

      notFound = (phone) ->
        Alert
          title: "未找到手机号#{phone}"
          content: '请修改条件进行查找。'
          placement: "top-left"
          type: "danger"
          container: '.well'
          duration: 3

      cardNotFound = (card) ->
        Alert
          title: "未找到卡号#{card}"
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

      scope.queryCard = (card) ->
        scope.relationship = Relationship.get card: card, ->
            scope.navigateTo(card, 'card')
          , (res) ->
            cardNotFound(card)

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

      scope.goToSchool = ->
        location.path '/main/phone_management'
  ]

.controller 'OpShowTeacherCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', '$alert', 'allEmployeesService', 'schoolService', 'employeePhoneService',
    (scope, rootScope, stateParams, location, Alert, Employee, School, EmployeePassword) ->
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

      scope.resetPassword = (person) ->
        person.new_password = _.last(person.phone, 8).join('')
        EmployeePassword.save person, ->
          Alert
            title: '重置成功'
            content: "登录名为#{person.login_name}的教师密码重置为手机号码后八位。"
            placement: "top"
            type: "success"
            container: '.well'
            duration: 3
  ]

.controller 'OpShowCardCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'relationshipSearchService', 'schoolService',
    (scope, rootScope, stateParams, location, Relationship, School) ->

      scope.relationship = Relationship.get card: stateParams.card, ->
        scope.school = School.get school_id: scope.relationship.parent.school_id
  ]

