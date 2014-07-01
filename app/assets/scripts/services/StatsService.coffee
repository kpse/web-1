'use strict'


angular.module('kulebaoAdmin')
.factory 'sessionStatsService', ['$resource', ($resource) ->
  $resource '/api/v1/statistics/session/:school_id',
    {
      school_id: '@school_id'
    }
]

angular.module('kulebaoAdmin')
.factory 'historyStatsService', ['$resource', ($resource) ->
  $resource '/api/v1/statistics/history/:school_id',
    {
      school_id: '@school_id'
    }
]

angular.module('kulebaoAdmin')
.factory 'StatsService', ['$resource', ($resource) ->
  (name) ->
    $resource '/api/v1/statistics/'+name+'/:school_id',
      {
        school_id: '@school_id'
      }
]


