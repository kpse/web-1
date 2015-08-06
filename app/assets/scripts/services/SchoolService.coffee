'use strict'

classService = ($resource) ->
  $resource '/kindergarten/:school_id/class/:class_id',
    {
      school_id: '@school_id'
      class_id: '@class_id'
    }

angular.module('kulebao.services')
.factory('classService', ['$resource', classService])

classManagerService = ($resource) ->
  $resource '/kindergarten/:school_id/class/:class_id/manager',
    {
      school_id: '@school_id'
      class_id: '@class_id'
    }

angular.module('kulebao.services')
.factory('classManagerService', ['$resource', classManagerService])



angular.module('kulebao.services')
.factory 'schoolService', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id',
    {
      school_id: '@school_id'
    }, {
      'get':    {method:'GET', cache: true}
    }
]

angular.module('kulebao.services')
.factory 'schoolPaginationService', ['$resource', ($resource) ->
  $resource '/api/v2/kindergarten', {}
]

angular.module('kulebao.services')
.factory 'schoolPreviewService', ['$resource', ($resource) ->
  $resource '/api/v2/kindergarten_preview', {}
]

adminCreatingService = ($resource) ->
  $resource '/kindergarten'

angular.module('kulebao.services')
.factory('adminCreatingService', ['$resource', adminCreatingService])

angular.module('kulebao.services')
.service 'accessClassService',
  ['$location', (location) ->
    (clazzes) ->
      location.path "#{location.path()}/class/#{clazzes[0].class_id}/list" if (location.path().indexOf('/class/') < 0) && clazzes.length > 0
      location.path '/default' if clazzes.length == 0
  ]

angular.module('kulebao.services')
.service 'schoolConfigService', ($resource) ->
  $resource '/api/v2/school_config/:school_id',
    {
      school_id: '@school_id'
    }

angular.module('kulebao.services')
.service 'schoolConfigExtractService', ->
  (data, name, defaultValue = '') ->
    config = _.find data, (item) -> item.name == name
    if config? then config.value else defaultValue


