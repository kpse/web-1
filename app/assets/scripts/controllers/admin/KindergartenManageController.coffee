class Controller
  constructor: (scope, $rootScope, $stateParams, School, location, Employee) ->

    @kindergarten = School.get school_id: $stateParams.kindergarten

    @adminUser = Employee.get()


    @isSelected = (tab)->
      tab is $rootScope.tabName

    scope.goParents = ->
      if location.path().indexOf("parents/class") < 0
        location.path('/kindergarten/' + $stateParams.kindergarten + '/parents')
      else
        location.path(location.path().replace(/\/[^\/]+$/, '/list'))

angular.module('kulebaoAdmin').controller 'KgManageCtrl', ['$scope', '$rootScope', '$stateParams', 'schoolService', '$location', 'employeeService', Controller]