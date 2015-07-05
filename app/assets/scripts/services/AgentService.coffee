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
    $resource '/api/v4/agent/:agent_id/kindergarten/:kg',
      {
        agent_id: "@agent_id"
        kg: "@kg"
      }
]

.factory 'agentAdService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/commercial/:id',
      {
        agent_id: "@agent_id"
        id: "@id"
      }
]

.factory 'agentAdPreviewService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/commercial/:id/preview',
      {
        agent_id: "@agent_id"
        id: "@id"
      }
]

.factory 'agentAdApproveService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/commercial/:id/publish',
      {
        agent_id: "@agent_id"
        id: "@id"
      }
]

.factory 'agentAdRejectService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/commercial/:id/reject',
      {
        agent_id: "@agent_id"
        id: "@id"
      }
]

.factory 'agentAdInSchoolService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/kindergarten/:kg/commercial/:id',
      {
        agent_id: "@agent_id"
        kg: "@kg"
        id: "@id"
      }
]

