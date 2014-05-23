'use strict'

chatSessionService = ($resource) ->
  $resource '/kindergarten/:school_id/session/:topic',
    {
      school_id: '@school_id'
      topic: '@topic'
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
  $resource '/kindergarten/:school_id/history/:topic',
    {
      school_id: '@school_id'
      topic: '@child_id'
    }

angular.module('kulebaoAdmin')
.factory('historyService', ['$resource', historyService])