'use strict'

chatSessionService = ($resource) ->
  $resource '/kindergarten/:school_id/session/:topic/record/:rid',
    {
      school_id: '@school_id'
      topic: '@topic'
      rid: '@id'
    }

angular.module('kulebaoAdmin')
.factory('chatSessionService', ['$resource', chatSessionService])

senderService = ($resource) ->
  $resource '/kindergarten/:school_id/sender/:id',
    {
      school_id: '@school_id'
      sender_id: '@sender.id'
      type: '@sender.type'
    }

angular.module('kulebaoAdmin')
.factory('senderService', ['$resource', senderService])



historyService = ($resource) ->
  $resource '/kindergarten/:school_id/history/:topic/record/:rid',
    {
      school_id: '@school_id'
      topic: '@topic'
      rid: '@id'
    }

angular.module('kulebaoAdmin')
.factory('historyService', ['$resource', historyService])