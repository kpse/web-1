'use strict'

angular.module("kulebao.directives").directive "klMediaPreview",
  ['imageCompressService',
    (Compress) ->
      restrict: "EA"
      scope:
        media: "="
        deleteCallback: "="
        height: "@"
        width: "@"
        clickable: "@"
        form: "="

      link: (scope, element) ->
        scope.$watch 'media.url', ( (n, o) ->
          scope.refresh()
        ), true

        scope.compress = (url, width, height) ->
          if scope.isImage(url)
            Compress.compress(url, width, height)
          else
            url

        scope.isImage = (url) ->
          url && !scope.isAudio(url) && !scope.isVideo(url)

        scope.isAudio = (url) ->
          url && /\.(mp3|amr)$/i.test(url)

        scope.isVideo = (url) ->
          url && /\.(mp4)$/i.test(url)

        scope.refresh = ->
          return unless scope.media?
          scope.url = scope.compress scope.media.url, scope.width, scope.height
          scope.type = scope.media.type

        scope.deleteThisImage = (media) ->
          scope.deleteCallback(media) if scope.deleteCallback?
          scope.form.$setDirty() if scope.form?

      templateUrl: (tElement, tAttrs) ->
        if tAttrs.clickable
          "/templates/directives/kl_media_preview.html"
        else
          "/templates/directives/kl_media_preview_no_click.html"
  ]