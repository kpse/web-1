'use strict'

chatSessionService = ($resource) ->
  $resource '/kindergarten/:school_id/session/:topic_id',
    {
      school_id: '@school_id'
      topic_id: '@topic_id'
    }

angular.module('kulebaoAdmin')
.factory('chatSessionService', ['$resource', chatSessionService])