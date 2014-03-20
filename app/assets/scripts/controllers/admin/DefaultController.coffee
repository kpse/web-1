angular.module('kulebaoAdmin').controller 'DefaultCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$location', 'employeeService',
    (scope, $rootScope, $stateParams, School, location, Employee) ->
      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: scope.adminUser.school_id, ->
          location.path('/kindergarten/' + scope.kindergarten.school_id)
  ]

angular.module('kulebaoAdmin').controller 'ExpiredCtrl',
  ['$scope', 'employeeService',
    (scope, Employee) ->
      scope.adminUser = Employee.get()
  ]