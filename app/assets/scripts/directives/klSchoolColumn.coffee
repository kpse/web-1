'use strict'

angular.module("kulebao.directives").directive "klSchoolColumn",
   ->
      return (
        restrict: "A"
        scope:
          user: "="
          school: "@klSchoolColumn"

        link: (scope) ->
          scope.clickable = ->
            scope.user.privilege_group == 'operator'

        templateUrl: '/templates/directives/kl_school_column.html'
      )

