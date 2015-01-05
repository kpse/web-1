'use strict'

angular.module('kulebao.services')
.factory 'batchParentsService', ['$resource',
  ($resource) ->
    $resource '/api/v1/batch_import/parents'
]
.factory 'batchChildrenService', ['$resource',
  ($resource) ->
    $resource '/api/v1/batch_import/children'
]
.factory 'batchRelationshipService', ['$resource',
  ($resource) ->
    $resource '/api/v1/batch_import/relationships'
]
