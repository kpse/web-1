'use strict'

angular.module('kulebao.services')
.factory('relationshipService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/relationship/:card',
      {
        school_id: '@school_id'
        card: '@card'
      }
]

).factory('relationshipSearchService', ['$resource',
  ($resource) ->
    $resource '/api/v2/relationship/:card',
      {
        card: '@card'
      }
]

).factory('cardCheckService', ['$resource',
  ($resource) ->
    $resource 'api/v1/card_check', {}, {
      'check':
        method: 'POST'
    }
]
).factory('cardCheckServiceV3', ['$resource',
  ($resource) ->
    $resource 'api/v3/kindergarten/:school_id/card_check/:card', {}, {
      'check':
        method: 'GET'
        params:
          card: '@card'
          school_id: '@school_id'
    }
])