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
    $resource '/api/v4/agent/:agentId/kindergarten/:kg',
      {
        agentId: "@agentId"
        kg: "@kg"
      }
  ]
.factory 'agentAdService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agentId/commercial/:ad',
      {
        agentId: "@agentId"
        ad: "@ad"
      }
  ]
.factory 'agentAdInSchoolService', ['$resource',
    ($resource) ->
      $resource '/api/v4/agent/:agentId/kindergarten/:kg/commercial/:id',
        {
          agentId: "@agentId"
          kg: "@kg"
          id: "@id"
        }
  ]

