'use strict'

angular.module('kulebao.directives').directive 'klFancyBox',
  ['$http', '$compile', '$sce', ($http, $compile, $sce) ->
    return (
      restrict: 'A'
      link: ($scope) ->
        $scope.openFancybox = (url, all) ->
          originalUrl = url.replace(/\?.*$/, '')
          allImages = _.map all, (c) -> {title: '', href: c.url.replace(/\?.*$/, '')}
          leftPart = _.dropWhile allImages, (c) -> c.href != originalUrl
          rightPart = _.takeWhile allImages, (c) -> c.href != originalUrl
          $.fancybox.open leftPart.concat rightPart

        $scope.openVideoFancybox = (videoUrl) ->
          $scope.videoUrl = $sce.trustAsResourceUrl(videoUrl)
          $http.get('templates/directives/fancy_video.html').then (response) ->
            if response.status == 200
              template = angular.element(response.data)
              compiledTemplate = $compile(template)($scope)
              $.fancybox.open
                content: compiledTemplate
                type: 'html'

    )]