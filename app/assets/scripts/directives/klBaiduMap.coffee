'use strict'

angular.module("kulebao.directives").directive "klBaiduMap", ->
  return (
    restrict: "A"
    scope:
      klBaiduMap: "="

    link: (scope, element, attrs, c) ->
      scope.map = new BMap.Map attrs.id
      zoomControl=new BMap.ZoomControl()
      scope.map.addControl(zoomControl)
      scaleControl=new BMap.ScaleControl()
      scope.map.addControl(scaleControl)

      scope.$watch "klBaiduMap", ((newVals, oldVals) ->
        scope.render scope.klBaiduMap if newVals?
      ), true

      scope.render = (model) ->
        point = new BMap.Point model.longitude, model.latitude
        scope.map.centerAndZoom point, 18
        myIcon = new BMap.Icon("http://api.map.baidu.com/mapCard/img/location.gif",
          new BMap.Size(14, 23)
          anchor: new BMap.Size(7, 25))

        marker = new BMap.Marker point, icon: myIcon
        scope.map.addOverlay marker

  )