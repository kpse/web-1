'use strict'

principalService = ($resource) ->
  $resource '/kindergarten/:school_id/principal',
    {
      school_id: '@school_id'
    }

angular.module('kulebao.services')
.factory('principalService', ['$resource', principalService])