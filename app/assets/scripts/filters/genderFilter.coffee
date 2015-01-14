'use strict'

angular.module('kulebao.filters')
.filter 'gender', () ->
    (gender) ->
      return '男' if gender is undefined
      if gender == 1 then '男' else '女'

