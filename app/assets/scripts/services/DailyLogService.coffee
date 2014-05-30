'use strict'

dailyLogService = ($resource) ->
  $resource '/kindergarten/:school_id/dailylog',
    {
      school_id: '@school_id'
    }

angular.module('kulebaoAdmin')
.factory('dailyLogService', ['$resource', dailyLogService])