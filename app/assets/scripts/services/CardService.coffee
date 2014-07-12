'use strict'

angular.module('kulebao.services')
.factory 'cardService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/card/:card_id',
      {
        school_id: '@school_id'
        card_id: '@card_id'
      }
  ]