'use strict'

readRecordService = ($resource) ->
  $resource '/kindergarten/:school_id/session/:topic/reader/:reader',
    {
      school_id: '@school_id'
      topic: '@topic'
      reader: '@reader'
    }

angular.module('kulebao.services')
.factory('readRecordService', ['$resource', readRecordService])