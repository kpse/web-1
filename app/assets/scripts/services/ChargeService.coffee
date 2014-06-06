'use strict'

chargeService = ($resource) ->
  $resource '/kindergarten/:school_id/charge',
    {
      school_id: '@school_id'
    }

angular.module('kulebaoAdmin')
.factory('chargeService', ['$resource', chargeService])

activeCountService = ($resource) ->
  $resource '/kindergarten/:school_id/active',
    {
      school_id: '@school_id'
    }

angular.module('kulebaoAdmin')
.factory('activeCountService', ['$resource', activeCountService])