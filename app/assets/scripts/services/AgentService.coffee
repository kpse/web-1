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

.factory 'agentContractorService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/contractor/:id',
      {
        agent_id: "@agent_id"
        id: "@id"
      }, {
        preview:
          url: '/api/v4/agent/:agent_id/contractor/:id/preview'
          method: 'POST'
        approve:
          url: '/api/v4/agent/:agent_id/contractor/:id/publish'
          method: 'POST'
        reject:
          url: '/api/v4/agent/:agent_id/contractor/:id/reject'
          method: 'POST'
      }

]

.factory 'agentActivityService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/contractor/:contractor_id/activity/:id',
      {
        agent_id: "@agent_id"
        contractor_id: "@contractor_id"
        id: "@id"
      }, {
        preview:
          url: '/api/v4/agent/:agent_id/contractor/:contractor_id/activity/:id/preview'
          method: 'POST'
        approve:
          url: '/api/v4/agent/:agent_id/contractor/:contractor_id/activity/:id/publish'
          method: 'POST'
        reject:
          url: '/api/v4/agent/:agent_id/contractor/:contractor_id/activity/:id/reject'
          method: 'POST'
      }

]

.factory 'agentRawActivityService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/activity/:id',
      {
        agent_id: "@agent_id"
        id: "@id"
      }, {
        preview:
          url: '/api/v4/agent/:agent_id/activity/:id/preview'
          method: 'POST'
        approve:
          url: '/api/v4/agent/:agent_id/activity/:id/publish'
          method: 'POST'
        reject:
          url: '/api/v4/agent/:agent_id/activity/:id/reject'
          method: 'POST'
      }

]

.factory 'agentAdInSchoolService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/kindergarten/:kg/contractor/:id',
      {
        agent_id: "@agent_id"
        kg: "@kg"
        id: "@id"
      }
]

.factory 'agentPasswordService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/password',
      {
        agent_id: "@agent_id"
      }
]

.factory 'agentResetPasswordService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/password_reset',
      {
        agent_id: "@agent_id"
      }
]

.factory 'agentStatsService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/statistics',
      {
        agent_id: "@agent_id"
      }
]

