'use strict'

angular.module('kulebao.filters')
.filter 'cardDisplay', () ->
    (card) ->
      if 'f' == _.first card
        '暂无卡号'
      else
        card

