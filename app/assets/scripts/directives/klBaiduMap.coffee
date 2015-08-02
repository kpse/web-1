'use strict'

angular.module("kulebao.directives").directive "klBaiduMap", ->
  restrict: "A"
  scope:
    klBaiduMap: "="
    clickable: "=?"
    hasMarker: "=?marker"

  link: (scope, element, attrs, c) ->
    scope.clickable = scope.clickable || false
    scope.hasMarker = scope.hasMarker || false
    scope.map = new BMap.Map attrs.id
    scope.map.addControl(new BMap.ScaleControl())
    scope.map.addControl(new BMap.NavigationControl())
    scope.map.addControl(new BMap.MapTypeControl())

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
      scope.map.addOverlay marker if scope.hasMarker

      clickHandler = (e)->
#        e.point.lng + "," + e.point.lat
        scope.map.removeOverlay scope.marker
        scope.marker = new BMap.Marker e.point, icon: myIcon
        scope.map.addOverlay scope.marker
        # 创建地理编码实例
        myGeo = new (BMap.Geocoder)
        # 根据坐标得到地址描述
        myGeo.getLocation new (BMap.Point)(e.point.lng, e.point.lat), (result) ->
          if result?
            opts =
              width: 100
              height: 50
              title: '位置拾取'
            infoWindow = new (BMap.InfoWindow)(result.address, opts)
            # 创建信息窗口对象
            scope.map.openInfoWindow infoWindow, scope.map.pixelToPoint(_.assign e.pixel, y: e.pixel.y - 20)

      scope.map.addEventListener "click", clickHandler if scope.clickable
