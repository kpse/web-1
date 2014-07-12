'use strict'

angular.module('kulebao.directives').directive 'ngBkFocus',
  ($timeout) ->
    restrict: "A"
    link: (scope, element, attrs) ->
      $timeout ->
          element[0].focus() if scope.$eval(attrs['ngBkFocus']) || attrs['ngBkFocus'] is ''
        , 500, false