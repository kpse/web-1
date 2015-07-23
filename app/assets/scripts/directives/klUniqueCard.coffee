'use strict'

angular.module("kulebao.directives").directive "klUniqueCard",
  ['cardCheckService',
    (CardCheck) ->
      return (
        restrict: "A"
        require: 'ngModel'
        scope:
          klUniqueCard: "="

        link: (scope, element, attrs, c) ->
          scope.$watch 'klUniqueCard.card', (n) ->
            return unless n?
            scope.check(scope.klUniqueCard) if n.length == 10

          scope.check = (relationship) ->
            return unless relationship?
            c.$setValidity 'unique', true
            c.$setValidity 'registered', true
            valid = CardCheck.check relationship, (->
              if valid.error_code == 0
                c.$setValidity 'unique', true
                c.$setValidity 'registered', true
              else if valid.error_code == 2
                c.$setValidity 'registered', false
              else
                c.$setValidity 'unique', false
            ), ->
              c.$setValidity 'unique', false
              c.$setValidity 'registered', false

      )
  ]