'use strict'

angular.module('kulebao.services')
.factory( 'chatSessionService', ['$resource',
    ($resource) ->
      $resource '/kindergarten/:school_id/session/:topic/record/:rid',
        {
          school_id: '@school_id'
          topic: '@topic'
          rid: '@id'
        }
]
).factory( 'senderService', ['$resource',
    ($resource) ->
      $resource '/kindergarten/:school_id/sender/:id',
        {
          school_id: '@school_id'
          sender_id: '@sender.id'
          type: '@sender.type'
        }
]
).factory( 'historyService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/history/:topic/record/:rid',
      {
        school_id: '@school_id'
        topic: '@topic'
        rid: '@id'
      }
]
).factory( 'historyShareService', ['$resource',
  ($resource) ->
    $resource '/api/v3/kindergarten/:school_id/history/:topic/record/:rid/share',
      {
        school_id: '@school_id'
        topic: '@topic'
        rid: '@id'
      }
])