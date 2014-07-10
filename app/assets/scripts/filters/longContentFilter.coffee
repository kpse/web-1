'use strict'

angular.module('kulebaoAdmin')
.filter 'truncate', ['$filter', ($filter) ->
    (content, length) ->
      return '' unless content?
      exp = if length? then length else 1000
      $filter('limitTo')(content, "#{exp}") + if content.length > length then '……' else ''

  ]