'use strict'

chatSessionService = ($resource) ->
  $resource '/kindergarten/:school_id/session/:topic',
    {
      school_id: '@school_id'
      topic: '@topic'
    }

angular.module('kulebaoAdmin')
.factory('chatSessionService', ['$resource', chatSessionService])