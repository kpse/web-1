'use strict'

#/api/v2/kindergarten/93740362/employee/3_93740362_1122/session

angular.module('kulebao.services')
.factory 'readRecordService', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id/session/:topic/reader/:reader',
    {
      school_id: '@school_id'
      topic: '@topic'
      reader: '@reader'
    }
]

.factory 'employeeSessionService', ['$resource', ($resource) ->
  $resource '/api/v2/kindergarten/:school_id/employee/:reader/session',
    {
      school_id: '@school_id'
      reader: '@reader'
    }
]

.factory 'employeeReadService', ['$resource', ($resource) ->
  $resource '/api/v2/kindergarten/:school_id/employee/:reader/read_session',
    {
      school_id: '@school_id'
      reader: '@reader'
    }
]
