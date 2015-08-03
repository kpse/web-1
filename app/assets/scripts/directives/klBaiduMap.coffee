'use strict'

angular.module("kulebao.directives").directive "klBaiduMap", ->
  restrict: "A"
  scope:
    klBaiduMap: "="
    clickable: "=?"

  link: (scope, element, attrs, c) ->
    scope.clickable = scope.clickable || false
    scope.hasMarker = scope.hasMarker || false

    scope.$watch "klBaiduMap", ((newVals, oldVals) ->
      scope.render scope.klBaiduMap if newVals?
    ), true

    scope.render = (model) ->
      map = new BMap.Map attrs.id
      return unless map.Q
      map.addControl(new BMap.ScaleControl())
      map.addControl(new BMap.NavigationControl())
      map.addControl(new BMap.MapTypeControl())
      point = new BMap.Point model.longitude, model.latitude
      map.centerAndZoom point, 18
      myIcon = new BMap.Icon("http://api.map.baidu.com/mapCard/img/location.gif",
        new BMap.Size(14, 23)
        anchor: new BMap.Size(7, 25))

      clickHandler = (e)->
        map.removeOverlay scope.marker
        scope.marker = new BMap.Marker e.point, icon: myIcon
        map.addOverlay scope.marker
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
            map.openInfoWindow infoWindow, map.pixelToPoint(_.assign e.pixel, y: e.pixel.y - 20)
            scope.klBaiduMap.result = result

      map.addEventListener "click", clickHandler if scope.clickable

      clickHandler point: point, pixel: map.pointToPixel(point)