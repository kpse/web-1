angular.module('kulebaoAdmin').controller 'DefaultCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$location', 'employeeService',
    (scope, $rootScope, $stateParams, School, location, Employee) ->
      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: scope.adminUser.school_id, ->
          location.path "/kindergarten/#{scope.kindergarten.school_id}/welcome"
        , (res) ->
          location.path "/#{res.status}"
      , (res) ->
        location.path "/#{res.status}"
  ]

.controller 'ExpiredCtrl',
  ['$scope', 'employeeService',
    (scope, Employee) ->
      scope.adminUser = Employee.get()
  ]

.controller 'WelcomeCtrl',
  ['$scope', '$rootScope', '$stateParams', 'chargeService', 'parentService',
   'childService', 'classService',
    (scope, $rootScope, stateParams, Charge, Parent, Child, Class) ->

      scope.kindergarten.charge = Charge.query school_id: stateParams.kindergarten, (data)->
        scope.kindergarten.charge = data[0]
        if scope.kindergarten.charge.status == 1
          scope.chargeInfo = '全校已开通( ' + scope.kindergarten.charge.used + ' / ' + scope.kindergarten.charge.total_phone_number + ' 人)'
        else
          scope.chargeInfo = '学校未开通手机幼乐宝服务，请与幼乐宝服务人员联系'
      scope.parents = Parent.query school_id: stateParams.kindergarten
      scope.children = Child.query school_id: stateParams.kindergarten
      scope.classes = Class.query school_id: stateParams.kindergarten, (classes)->
        scope.noAccess = classes.length == 0
  ]