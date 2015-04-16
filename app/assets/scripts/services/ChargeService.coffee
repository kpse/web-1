'use strict'

angular.module('kulebao.services')
.factory 'chargeService', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id/charge',
    {
      school_id: '@school_id'
    }

]

angular.module('kulebao.services')
.factory 'activeCountService', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id/active',
    {
      school_id: '@school_id'
    }, {
      get: cache: true
    }
]

angular.module('kulebao.services')
.factory 'videoProviderService', ['$resource', ($resource) ->
  $resource '/api/v1/video_provider/:school_id',
    {
      school_id: '@school_id'
    }
]

angular.module('kulebao.services')
.factory 'videoMemberService', ['$resource', ($resource) ->
  $resource '/api/v1/kindergarten/:school_id/video_member/:id',
    {
      school_id: '@school_id'
      id: '@id'
    }
]

angular.module('kulebao.services')
.factory 'videoMemberCheckingService', ['$resource', ($resource) ->
  $resource '/api/v1/kindergarten/:school_id/check_video_member',
    {
      school_id: '@school_id'
    }
]