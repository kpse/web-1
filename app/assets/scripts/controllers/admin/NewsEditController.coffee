'use strict'

angular.module('kulebaoAdmin').controller 'AddNewsCtrl',
  ['$scope', '$rootScope', '$location', 'adminNewsService',
   '$stateParams', 'GroupMessage', 'schoolService',
    (scope, $rootScope, $location, adminNewsService, $stateParams, GroupMessage, School) ->
      scope.adminUser =
        id: 1
        name: '学校某老师'

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
        news.published = true
        news.$save ->
          scope.$emit 'refreshNews'

      scope.remove = (news) ->
        news.$delete admin_id: scope.adminUser.id, ->
          scope.$hide()
          scope.$emit 'refreshNews'

  ]