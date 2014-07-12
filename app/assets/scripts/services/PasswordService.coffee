'use strict'

passwordService = ($resource) ->
  $resource '/kindergarten/:school_id/employee/:phone/password',
    {
      school_id: '@school_id'
      phone: '@phone'
    }

angular.module('kulebao.services')
.factory('passwordService', ['$resource', passwordService])

passwordTokenService = ($resource) ->
  $resource '/ws/verify/phone/:phone',
    {
      phone: '@phone'
    }

angular.module('kulebao.services')
.factory('passwordTokenService', ['$resource', passwordTokenService])
