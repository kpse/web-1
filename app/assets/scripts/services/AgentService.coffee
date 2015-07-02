'use strict'

angular.module('kulebao.services')
.factory 'agentService', ['$resource', 'session',
    ($resource, Session) ->
      $resource '/api/v4/agent/:id',
        {
          id: Session.id
        }, {
          all:
            params:
              id: '@id'
          get:
            method: 'GET'
            cache: true
        }
  ]
.factory 'agentManagementService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:id',
      {
        id: '@id'
      }
  ]
.factory 'agentSchoolService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:id/kindergarten/:kg',
      {
        agentId: "@agentId"
        kg: "@school_id"
      }
  ]
.factory 'agentAdService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agentId/commercial/:id',
      {
        agentId: "@agentId"
        id: "@id"
      }
  ]
.factory 'agentAdInSchoolService', ['$resource',
    ($resource) ->
      $resource '/api/v4/agent/:agentId/kindergarten/:kg/commercial/:id',
        {
          agentId: "@agentId"
          kg: "@school_id"
          id: "@id"
        }
  ]

