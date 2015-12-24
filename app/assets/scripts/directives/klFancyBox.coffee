'use strict'

angular.module('kulebao.directives').directive 'klFancyBox', ($compile, $http) ->
  return (
    restrict: 'A'
    controller: ($scope) ->
      $scope.openFancybox = (url) ->
        originalUrl = url.replace(/\?.*$/, '')
        $.fancybox.open [{title: '', href: originalUrl}]

  )