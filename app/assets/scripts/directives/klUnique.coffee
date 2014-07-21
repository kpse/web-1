'use strict'

angular.module("kulebao.directives").directive "klUnique",
  ['phoneCheckService',
    (PhoneCheck) ->
      return (
        restrict: "A"
        require: 'ngModel'
        scope:
          klUnique: "="
          uniqueType: "@"
          uniqueIdentity: "@"

        link: (scope, element, attrs, c) ->
          scope.id = scope.uniqueIdentity || 'parent_id'
          scope.$watch 'klUnique.phone', (n) ->
            if !n? || n.length == 0
              c.$setValidity 'unique', true
            else
              scope.check(scope.klUnique)


          scope.check = (person) ->
            return unless person?
            PhoneCheck.check id: person[scope.id], phone: person.phone, employee: scope.uniqueType, ((valid)->
              if valid.error_code == 0
                c.$setValidity 'unique', true
              else
                c.$setValidity 'unique', false
            ), ->
              c.$setValidity 'unique', false

      )
  ]