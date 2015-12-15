'use strict'

angular.module("kulebao.directives").directive "klBaiduMap", ->
  restrict: "A"
  scope:
    klBaiduMap: "="
    clickable: "=?"
    upperForm: "=?form"

  link: (scope, element, attrs, c) ->
    scope.clickable = scope.clickable || false
    scope.hasMarker = scope.hasMarker || false

    scope.$watch "klBaiduMap", ((newVals, oldVals) ->
      scope.render scope.klBaiduMap if newVals?
    ), true

    myIcon = new BMap.Icon("http://api.map.baidu.com/mapCard/img/location.gif",
      new BMap.Size(14, 23)
      anchor: new BMap.Size(7, 25))

    clickHandler = (map) ->
      (e)->
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
            scope.upperForm.$setDirty() if scope.upperForm?

    createMap = (s) ->
      map = new BMap.Map attrs.id
      map.addControl(new BMap.ScaleControl())
      map.addControl(new BMap.NavigationControl())
      map.addControl(new BMap.MapTypeControl())
      s.map = map
      s.map
    scope.render = (model) ->
      map = if scope.map? then scope.map else createMap(scope)
      if model.city?
        map.setCurrentCity(model.city);
        # 创建地址解析器实例
        myGeo = new (BMap.Geocoder)
        # 将地址解析结果显示在地图上，并调整地图视野
        myGeo.getPoint model.address, ((point) ->
          point = point || new BMap.Point 116.404, 39.915
          map.centerAndZoom point, 13
          clickHandler(map) point: point, pixel: map.pointToPixel(point)
        ), model.city
      else
        point = new BMap.Point model.longitude, model.latitude
        map.centerAndZoom point, 17
        clickHandler(map) point: point, pixel: map.pointToPixel(point)

      map.addEventListener "click", clickHandler(map) if scope.clickable

