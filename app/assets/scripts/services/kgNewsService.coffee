'use strict'
newsService = ($resource) ->
  $resource '/kindergarten/:school_id/news/:news_id',
    {
      school_id: '@school_id'
      news_id: '@news_id'
    }
readService = ($resource) ->
  $resource '/kindergarten/:school_id/parent/:parent_id/news/:news_id',
    {
      school_id: '@school_id'
      news_id: '@news_id'
      parent_id: '@parent_id'
    },
    {
      markRead:
        method: 'POST'
    }
readingStatService = ($resource) ->
  $resource '/kindergarten/:school_id/admin/:admin_id/news_reading_count/:news_id',
    {
      school_id: '@school_id'
      news_id: '@news_id'
      admin_id: '@admin_id'
    },
    {
      readingCount: method: 'GET'
    }

newsReadService = ($resource) ->
  $resource '/api/v2/kindergarten/:school_id/news/:news_id/reader',
    {
      school_id: '@school_id'
      news_id: '@news_id'
    }

newsServiceV2 = ($resource) ->
  $resource '/api/v2/kindergarten/:school_id/news/:news_id',
    {
      school_id: '@school_id'
      news_id: '@news_id'
      tag: '1'
    }


angular.module('kulebao.services')
.factory('newsService', ['$resource', newsService])
.factory('readService', ['$resource', readService])
.factory('readingStatService', ['$resource', readingStatService])
.factory('newsReadService', ['$resource', newsReadService])
.factory('newsServiceV2', ['$resource', newsServiceV2])