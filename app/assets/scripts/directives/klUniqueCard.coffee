'use strict'

angular.module("kulebao.directives").directive "klUniqueCard",
  ['cardCheckService',
    (CardCheck) ->
      return (
        restrict: "A"
        require: 'ngModel'
        scope:
          klUnique: "="

        link: (scope, element, attrs, c) ->
          scope.$watch 'klUniqueCard.card', (n) ->
            return unless n?
            scope.check(scope.klUnique)

          scope.check = (relationship) ->
            return unless relationship?
            valid = CardCheck.check id: relationship.id, card: relationship.card, (->
              if valid.error_code == 0
                c.$setValidity 'unique', true
              else
                c.$setValidity 'unique', false
            ), ->
              c.$setValidity 'unique', false

      )
  ]