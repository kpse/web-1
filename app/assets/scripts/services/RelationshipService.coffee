'use strict'

angular.module('kulebao.services')
.factory 'relationshipService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/relationship/:card',
      {
        school_id: '@school_id'
        card: '@card'
      }
]

angular.module('kulebao.services')
.factory 'cardCheckService', ['$resource',
  ($resource) ->
    $resource 'api/v1/card_check', {}, {
      'check':
        method: 'POST'
    }
]