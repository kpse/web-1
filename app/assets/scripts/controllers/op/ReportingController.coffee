angular.module('kulebaoOp').controller 'OpReportingCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http', 'parentService', 'childService', 'employeeService',
   'allEmployeesService', 'classService', 'schoolService',
    (scope, rootScope, $stateParams, $http, Parent, Child, CurrentUser, Employee, Class, School) ->

      rootScope.tabName = 'reporting'
      scope.adminUser = CurrentUser.get()

      scope.loading = true
      scope.allClasses = []
      scope.allEmployees = []
      scope.allChildren = []
      scope.allParents = []
      scope.kindergartens = School.query ->
        _.forEach scope.kindergartens, (k) ->
          k.classes = Class.query school_id: k.school_id, ->
            scope.allClasses = scope.allClasses.concat k.classes
          k.parents = Parent.query school_id: k.school_id, ->
            scope.allParents = scope.allParents.concat k.parents
          k.children = Child.query school_id: k.school_id, ->
            scope.allChildren = scope.allChildren.concat k.children
          k.employees = Employee.query school_id: k.school_id, ->
            scope.allEmployees = scope.allEmployees.concat k.employees
        scope.loading = false



  ]




