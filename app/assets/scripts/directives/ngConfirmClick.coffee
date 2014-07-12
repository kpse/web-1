'use strict'

angular.module('kulebao.directives').directive 'ngConfirmClick', ->
  restrict: "A"
  link: (scope, element, attrs) ->
    element.bind "click", (e) ->
      e.stopPropagation()
      message = attrs.ngConfirmMsg
      if message and confirm(message)
        scope.$apply attrs.ngConfirmClick