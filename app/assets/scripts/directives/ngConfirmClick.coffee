'use strict'

ngConfirmClick = () ->
  restrict: "A"
  link: (scope, element, attrs) ->
    element.bind "click", (e) ->
      e.stopPropagation()
      message = attrs.ngConfirmMsg
      if message and confirm(message)
        scope.$apply attrs.ngConfirmClick


angular.module('kulebaoApp').directive('ngConfirmClick', ngConfirmClick)