'use strict'

chargeService = ($resource) ->
  $resource '/kindergarten/:school_id/charge',
    {
      school_id: '@school_id'
    }

angular.module('kulebaoAdmin')
.factory('chargeService', ['$resource', chargeService])