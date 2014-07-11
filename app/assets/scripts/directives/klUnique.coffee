'use strict'

angular.module("kulebao.directives").directive "klUnique",
  ['phoneCheckService',
    (PhoneCheck) ->
      return (
        restrict: "A"
        require: 'ngModel'
        scope:
          klUnique: "="

        link: (scope, element, attrs, c) ->
          scope.$watch 'klUnique.phone', (n) ->
            return unless n?
            scope.check(scope.klUnique)

          scope.check = (parent) ->
            return unless parent?
            valid = PhoneCheck.check id: parent.parent_id, phone: parent.phone, (->
              if valid.error_code == 0
                c.$setValidity 'unique', true
              else
                c.$setValidity 'unique', false
            ), ->
              c.$setValidity 'unique', false

      )
  ]