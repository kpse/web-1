'use strict'

angular.module("kulebao.directives").directive "klUniqueCard",
  ['cardCheckServiceV3',
    (CardCheck) ->
      return (
        restrict: "A"
        require: 'ngModel'
        scope:
          klUniqueCard: "="

        link: (scope, element, attrs, c) ->
          scope.$watch 'klUniqueCard.card', (n, o) ->
            return unless n?
            return if n == o
            scope.check(scope.klUniqueCard) if n.length == 10

          scope.check = (relationship) ->
            return unless relationship?
            c.$setValidity 'unique', true
            c.$setValidity 'registered', true
            CardCheck.check relationship, (valid) ->
                if valid.error_code == 0
                  c.$setValidity 'unique', true
                  c.$setValidity 'registered', true
                else if valid.error_code == 3
                  c.$setValidity 'registered', false
                else
                  c.$setValidity 'unique', false
              , (err)->
                c.$setValidity 'unique', false
                c.$setValidity 'registered', false

      )
  ]