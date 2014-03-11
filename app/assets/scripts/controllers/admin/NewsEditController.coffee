'use strict'

angular.module('kulebaoAdmin').controller 'AddNewsCtrl',
  ['$scope', '$rootScope', '$location', 'adminNewsService',
   '$stateParams', 'GroupMessage', 'schoolService', 'employeeService'
    (scope, $rootScope, $location, adminNewsService, $stateParams, GroupMessage, School, Employee) ->
      scope.adminUser = Employee.get ->
        scope.kindergarten = School.get school_id: $stateParams.kindergarten, ->
          if $rootScope.editingNews is undefined
            scope.news = new adminNewsService
              school_id: scope.kindergarten.school_id
              admin_id: scope.adminUser.id
          else
            scope.news = $rootScope.editingNews
            delete $rootScope.editingNews

      scope.save = (news) ->
        news.$save ->
          scope.$hide()
          scope.$emit 'refreshNews'

      scope.publish = (news) ->
        scope.loading = true
        news.published = true
        news.$save ->
          scope.$emit 'refreshNews'
          scope.loading = false

      scope.remove = (news) ->
        scope.loading = true
        news.$delete admin_id: scope.adminUser.id, ->
          scope.$hide()
          scope.$emit 'refreshNews'
          scope.loading = false

  ]