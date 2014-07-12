'use strict'

angular.module('kulebao.services')
.factory 'adminNewsService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/admin/:admin_id/news/:news_id',
      {
        school_id: '@school_id'
        admin_id: 'reserved'
        news_id: '@news_id'
      }
]


angular.module('kulebao.services')
.factory 'adminNewsPreview', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id/admin/:admin_id/news/preview',
    {
      school_id: '@school_id'
      admin_id: 'reserved'
    }


]
