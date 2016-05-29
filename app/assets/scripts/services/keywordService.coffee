'use strict'

angular.module('kulebao.services')
.factory('keywordService', ['$resource',
  ($resource) ->
    $resource '/api/v8/kindergarten/:school_id/im_keyword/:id',
      {
        school_id: '@school_id'
        id: '@id'
      }
  ]
)

