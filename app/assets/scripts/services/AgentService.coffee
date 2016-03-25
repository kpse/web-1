angular.module('kulebao.services')
.factory('agentService', ['$resource', 'session',
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

).factory('agentManagementService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:id',
      {
        id: '@id'
      }
  ]

).factory('agentSchoolService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/kindergarten/:kg',
      {
        agent_id: "@agent_id"
        kg: "@kg"
      }
  ]

).factory('agentContractorService', ['$resource',
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
        active:
          url: '/api/v4/agent/:agent_id/contractor/:id/active'
          method: 'POST'
        deactive:
          url: '/api/v4/agent/:agent_id/contractor/:id/deactive'
          method: 'POST'
      }
  ]

).factory('agentActivityService', ['$resource',
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

).factory('agentRawActivityService', ['$resource',
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
        active:
          url: '/api/v4/agent/:agent_id/activity/:id/active'
          method: 'POST'
        deactive:
          url: '/api/v4/agent/:agent_id/activity/:id/deactive'
          method: 'POST'
      }
  ]

).factory('agentContractorInSchoolService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/kindergarten/:school_id/contractor/:id',
      {
        agent_id: "@agent_id"
        school_id: "@school_id"
        id: "@id"
      }
  ]

).factory('agentActivityInSchoolService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/kindergarten/:school_id/activity/:id',
      {
        agent_id: "@agent_id"
        school_id: "@school_id"
        id: "@id"
      }
  ]

).factory('agentPasswordService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/password',
      {
        agent_id: "@agent_id"
      }
  ]

).factory('agentResetPasswordService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/password_reset',
      {
        agent_id: "@agent_id"
      }
  ]
).factory('agentStatsService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/statistics/:id',
      {
        agent_id: "@agent_id"
        id: "@id"
      }
  ]
).factory('agentWeekStatsService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/weekly_statistics',
      {
        agent_id: "@agent_id"
      },
      {
        query:
          cache: true
          isArray: true
      }
  ]
).factory('agentSchoolDataService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/kindergarten/:school_id/active',
      {
        agent_id: "@agent_id"
        school_id: "@school_id"
      }, {
        get:
          method: 'GET'
          cache: true
      }
  ]
).factory('agentActivityEnrollmentService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent/:agent_id/activity/:id/enrollment',
      {
        agent_id: "@agent_id"
        id: "@id"
      }
  ]
).factory('agentStatsOperatorService', ['$resource',
  ($resource) ->
    $resource '/api/v4/agent_statistics',{}
  ]
).service 'totalActiveRateService', ->
  (data) ->
    result = if data.child_count == 0 then 0 else (data.logged_ever / (data.child_count * 1.5) * 100 ).toFixed 2
    if result > 100 then 100 else result

.service 'monthlyActiveRateService', ->
  (data) ->
    if !data? || !data.logged_ever? || data.logged_ever == 0
      0
    else
      (data.logged_once/data.logged_ever * 100).toFixed 2

.service 'agentLocationService', ->
    provinceOf: (address) ->
      return '' unless address?
      m = address.match(/^([^市省县区]+?省)/)
      m = address.match(/^([^市省县自治区]+?自治区)/) unless m?
      m = address.match(/^([^市省县自治特别行政区]+?特别行政区)/) unless m?
      (m && m[0]) || ''
    cityOf: (address) ->
      return '' unless address?
      m = address.match(/[^市省县区]+?[省|区]([^市省县区]+?自治州)/)
      m = address.match(/[^市省县区]+?[省|区]([^市省县区]+?市)/) unless m?
      m = address.match(/^([^市省县区]+?市)/) unless m?
      (m && m[1]) || ''
    countyOf: (address) ->
      return '' unless address?
      m = address.match(/自治州(.+?县)/)
      m = address.match(/市(.+?(县|区|市))/) unless m?
      (m && m[1]) || ''
