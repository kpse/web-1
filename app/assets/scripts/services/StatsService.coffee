'use strict'

sessionStatsService = ($resource) ->
  $resource '/api/v1/statistics/session/:school_id',
    {
      school_id: '@school_id'
    }

angular.module('kulebaoAdmin')
.factory('sessionStatsService', ['$resource', sessionStatsService])

historyStatsService = ($resource) ->
  $resource '/api/v1/statistics/history/:school_id',
    {
      school_id: '@school_id'
    }

angular.module('kulebaoAdmin')
.factory('historyStatsService', ['$resource', historyStatsService])

