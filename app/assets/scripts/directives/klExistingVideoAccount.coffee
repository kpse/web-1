'use strict'

angular.module("kulebao.directives").directive "klExistingVideoAccount",
  ['videoMemberCheckingService',
    (VideoMember) ->
      return (
        restrict: "A"
        require: 'ngModel'
        scope:
          ngModel: '='
          parent: '='

        link: (scope, element, attrs, form) ->
          scope.$watch attrs.ngModel, (n) ->
            if !n? || n.length < 32
              form.$setValidity 'notExisting', true
            else
              scope.check(scope.ngModel)


          scope.check = (account) ->
            return unless account?
            VideoMember.get account: account, school_id: scope.parent.school_id, ((valid)->
              if valid.error_code == 0
                form.$setValidity 'notExisting', true
              else
                form.$setValidity 'notExisting', false
            ), ->
              form.$setValidity 'notExisting', false

      )
  ]