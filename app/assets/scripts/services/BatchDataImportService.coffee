'use strict'

angular.module('kulebao.services')
.factory 'batchDataService', ['$resource',
  ($resource) ->
    (type) ->
      $resource "/api/v1/batch_import/#{type}"
]
