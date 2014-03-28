angular.module('kulebaoAdmin').controller 'DefaultCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$location', 'employeeService',
    (scope, $rootScope, $stateParams, School, location, Employee) ->
      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: scope.adminUser.school_id, ->
          location.path('/kindergarten/' + scope.kindergarten.school_id)
        , (res) ->
            location.path('/' + res.status)

  ]

angular.module('kulebaoAdmin').controller 'ExpiredCtrl',
  ['$scope', 'employeeService',
    (scope, Employee) ->
      scope.adminUser = Employee.get()
  ]

angular.module('kulebaoAdmin').controller 'WelcomeCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', 'employeeService', 'chargeService', 'parentService',
   'childService'
    (scope, $rootScope, stateParams, School, Employee, Charge, Parent, Child) ->
      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
          scope.kindergarten.charge = Charge.query school_id: stateParams.kindergarten, ->
            if scope.kindergarten.charge[0] && scope.kindergarten.charge[0].status == 1
              scope.chargeInfo = '全校已开通( ' + scope.kindergarten.charge[0].used + ' / ' + scope.kindergarten.charge[0].total_phone_number + ' 人)'
            else
              scope.chargeInfo = '学校未开通手机幼乐宝服务，请与幼乐宝服务人员联系'
          scope.parents = Parent.query school_id: stateParams.kindergarten
          scope.children = Child.query school_id: stateParams.kindergarten
  ]