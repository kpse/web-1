'use strict'

angular.module("kulebao.directives").directive "klMediaPreview",
  ['imageCompressService',
    (Compress) ->
      return (
        restrict: "EA"
        scope:
          media: "="
          height: "@"
          width: "@"
          clickable: "@"

        link: (scope, element) ->
          scope.$watch "media.url", ( (n, o) ->
            scope.refresh()
          ), true

          scope.compress = (url, width, height) ->
            if scope.isImage(url)
              Compress.compress(url, width, height)
            else
              url

          scope.isImage = (url) ->
            url && /\.(jpg|png)$/.test(url)

          scope.isAudio = (url) ->
            url && !scope.isImage(url)

          scope.refresh = ->
            return unless scope.media?
            scope.url = scope.compress scope.media.url, scope.width, scope.height
            scope.type = scope.media.type

        templateUrl: (tElement, tAttrs) ->
          if tAttrs.clickable
            "/templates/directives/kl_media_preview.html"
          else
            "/templates/directives/kl_media_preview_no_click.html"
      )
  ]