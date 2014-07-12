'use strict'

dailyLogService = ($resource) ->
  $resource '/kindergarten/:school_id/dailylog',
    {
      school_id: '@school_id'
    }

angular.module('kulebao.services')
.factory('dailyLogService', ['$resource', dailyLogService])

singleDailyLogService = ($resource) ->
  $resource '/kindergarten/:school_id/child/:child_id/dailylog',
    {
      school_id: '@school_id'
      child_id: '@child_id'
    }, {
      last:
        method: 'GET'
        params:
          most: 1
    }

angular.module('kulebao.services')
.factory('singleDailyLogService', ['$resource', singleDailyLogService])

angular.module('kulebao.services')
.factory 'statisticsDailyLogService', ['$resource', ($resource) ->
  $resource '/api/v1/statistics/dailylog/:school_id',
    {
      school_id: '@school_id'
    }
 ]
