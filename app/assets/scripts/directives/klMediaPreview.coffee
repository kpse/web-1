'use strict'

angular.module("kulebaoAdmin").directive "klMediaPreview",
  ['imageCompressService', '$compile', '$http', '$templateCache',
    (Compress, $compile, $http, $templateCache) ->
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


          scope.getTemplate = (contentType) ->
            if contentType?
              templateUrl = "/templates/directives/kl_media_preview.html"
            else
              templateUrl = "/templates/directives/kl_media_preview_no_click.html"

            $http.get templateUrl, cache: $templateCache

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

            loader = scope.getTemplate(scope.clickable)
            loader.success((html) ->
              element.html html
            ).then (res) ->
              element.replaceWith $compile(element.html())(scope)

      )
  ]