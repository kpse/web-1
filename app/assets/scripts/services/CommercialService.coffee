'use strict'

angular.module('kulebao.services')
.factory 'adService', ['$resource',
  ($resource) ->
    $resource '/api/v1/kindergarten/:school_id/ad/:id',
      {
        school_id: '@school_id'
        id: '@id'
      }
]

