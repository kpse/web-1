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

schoolService = ($resource) ->
  $resource '/kindergarten/:school_id',
    {
      school_id: '@school_id'
    }, {
      'get':    {method:'GET', cache: true}
    }


angular.module('kulebao.services')
.factory('schoolService', ['$resource', schoolService])

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

