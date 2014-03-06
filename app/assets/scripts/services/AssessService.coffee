'use strict'

assessService = ($resource) ->
  $resource '/kindergarten/:school_id/assess/:child_id',
    {
      school_id: '@school_id'
      child_id: '@child_id'
    }

angular.module('kulebaoAdmin')
.factory('assessService', ['$resource', assessService])