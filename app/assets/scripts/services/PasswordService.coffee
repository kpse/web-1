'use strict'

passwordService = ($resource) ->
  $resource '/kindergarten/:school_id/employee/:phone/password',
    {
      school_id: '@school_id'
      phone: '@phone'
    }

angular.module('kulebaoAdmin')
.factory('passwordService', ['$resource', passwordService])