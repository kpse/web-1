'use strict'

angular.module('kulebao.services')
.factory 'adminNewsService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/admin/:publisher_id/news/:news_id',
      {
        school_id: '@school_id'
        publisher_id: '@publisher_id'
        news_id: '@news_id'
      }
]
.factory 'adminNewsPreview', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id/admin/:publisher_id/news/preview',
    {
      school_id: '@school_id'
      publisher_id: '@publisher_id'
    }
]
.factory 'adminNewsServiceV2', ['$resource',
  ($resource) ->
    $resource '/api/v2/kindergarten/:school_id/admin/:publisher_id/news/:news_id',
      {
        school_id: '@school_id'
        publisher_id: '@publisher_id'
        news_id: '@news_id'
      }
]