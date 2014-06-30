'use strict'

angular.module("kulebaoAdmin").directive "d3Bars", [
  "d3"
  (d3) ->
    return (
      restrict: "EA"
      scope:
        data: "="
        label: "@"
        onClick: "&"

      link: (scope, iElement, iAttrs) ->
        svg = d3.select(iElement[0]).append("svg").attr("width", "100%")

        # on window resize, re-render d3 canvas
        window.onresize = ->
          scope.$apply()

        scope.$watch (->
          angular.element(window)[0].innerWidth
        ), ->
          scope.render scope.data


        # watch for data changes and re-render
        scope.$watch "data", ((newVals, oldVals) ->
          scope.render newVals
        ), true

        # define render function
        scope.render = (data) ->

          # remove all previous items before render
          svg.selectAll("*").remove()

          max = 150
          svg.attr "height", max


          svg.selectAll("rect").data(data).enter().append("rect").on("click", (d, i) ->
            scope.onClick item: d
          ).attr("width", 30).attr("height", max).attr("y", 0).style('fill', '#0f0').attr("x", (d, i) ->
            i * 85
          ).transition().duration(1000).attr "y", (d) ->
              max - d.count * 25

          svg.selectAll("text").data(data).enter().append("text").attr("fill", "#000").attr("x", (d, i) ->
            i * 85
          ).attr("y", 20).text (d) ->
            d[scope.label]

    )
]