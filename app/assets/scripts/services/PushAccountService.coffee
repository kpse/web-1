'use strict'

pushAccountService = ($resource) ->
  $resource '/api/v2/login_token/:phone',
    {
      phone: '@phone'
    }

angular.module('kulebao.services')
.factory('pushAccountService', ['$resource', pushAccountService])