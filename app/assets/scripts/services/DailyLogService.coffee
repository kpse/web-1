'use strict'

dailyLogService = ($resource) ->
  $resource '/kindergarten/:school_id/dailylog',
    {
      school_id: '@school_id'
    }

angular.module('kulebaoAdmin')
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

angular.module('kulebaoAdmin')
.factory('singleDailyLogService', ['$resource', singleDailyLogService])

angular.module('kulebaoAdmin')
.factory 'statisticsDailyLogService', ['$resource', ($resource) ->
  $resource '/api/v1/statistics/dailylog/:school_id',
    {
      school_id: '@school_id'
    }
 ]
