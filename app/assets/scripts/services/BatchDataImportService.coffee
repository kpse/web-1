'use strict'

angular.module('kulebao.services')
.factory 'batchDataService', ['$resource',
  ($resource) ->
    (type, school) ->
      if school?
        $resource "/api/v1/batch_import/#{school}/#{type}"
      else
        $resource "/api/v1/batch_import/#{type}"
]
