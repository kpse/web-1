angular.module('kulebaoOp').controller 'OpCtrl', ['$scope', '$rootScope', '$stateParams', 'employeeService',
  ($scope, $rootScope, stateParams, Employee) ->

    $scope.adminUser = Employee.get()

    $scope.isSelected = (tab)->
      tab == $rootScope.tabName

 ]