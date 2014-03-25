'use strict'

classService = ($resource) ->
  $resource '/kindergarten/:school_id/class/:class_id',
    {
      school_id: '@school_id'
      class_id: '@class_id'
    }

angular.module('kulebaoAdmin')
.factory('classService', ['$resource', classService])

classManagerService = ($resource) ->
  $resource '/kindergarten/:school_id/class/:class_id/manager',
    {
      school_id: '@school_id'
      class_id: '@class_id'
    }

angular.module('kulebaoAdmin')
.factory('classManagerService', ['$resource', classManagerService])

schoolService = ($resource) ->
  $resource '/kindergarten/:school_id',
    {
      school_id: '@school_id'
    }, {
      'get':    {method:'GET', cache: true}
    }


angular.module('kulebaoAdmin')
.factory('schoolService', ['$resource', schoolService])

adminCreatingService = ($resource) ->
  $resource '/kindergarten'

angular.module('kulebaoAdmin')
.factory('adminCreatingService', ['$resource', adminCreatingService])