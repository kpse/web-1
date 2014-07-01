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
.factory 'assignmentStatsService', ['$resource', ($resource) ->
  $resource '/api/v1/statistics/assignment/:school_id',
    {
      school_id: '@school_id'
    }
]


