'use strict'

angular.module("kulebao.directives").directive "klBaiduMap",  ->
      return (
        restrict: "A"
        scope:
          klBaiduMap: "="

        link: (scope, element, attrs, c) ->
          scope.map = new BMap.Map(attrs.id)
          scope.$watch "klBaiduMap", ((newVals, oldVals) ->
            scope.render scope.klBaiduMap if newVals?
          ), true

          scope.render = (model) ->
            scope.map.centerAndZoom(new BMap.Point(model.longitude, model.latitude), 18)
      )