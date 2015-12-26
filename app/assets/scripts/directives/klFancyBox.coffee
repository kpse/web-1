'use strict'

angular.module('kulebao.directives').directive 'klFancyBox', ->
  return (
    restrict: 'A'
    controller: ($scope) ->
      $scope.openFancybox = (url, all) ->
        originalUrl = url.replace(/\?.*$/, '')
        allImages = _.map all, (c) -> {title: '', href: c.url.replace(/\?.*$/, '')}
        leftPart = _.dropWhile allImages, (c) -> c.href != originalUrl
        rightPart = _.takeWhile allImages, (c) -> c.href != originalUrl
        $.fancybox.open leftPart.concat rightPart

  )