angular.module('kulebaoAdmin').controller 'KgManageCtrl',
  ['$scope', '$rootScope', '$stateParams', 'schoolService', '$location', 'employeeService',
    (scope, $rootScope, $stateParams, School, location, Employee) ->
      scope.kindergarten = School.get school_id: $stateParams.kindergarten

      scope.adminUser = Employee.get()


      scope.isSelected = (tab)->
        tab is $rootScope.tabName

      goPageWithClassesTab = (pageName)->
        if location.path().indexOf(pageName + '/class') < 0
          location.path('/kindergarten/' + $stateParams.kindergarten + '/' + pageName)
        else
          location.path(location.path().replace(/\/[^\/]+$/, '/list'))

      scope.goConversation = ->
        goPageWithClassesTab('conversation')

      scope.goAssignment = ->
        goPageWithClassesTab('assignment')

      scope.goBabyStatus = ->
        goPageWithClassesTab('baby-status')

  ]