'use strict'


angular.module('kulebao.services')
.factory 'pushAccountService', ['$resource',
    ($resource) ->
      $resource '/api/v2/login_token/:phone',
        {
          phone: '@phone'
        }
  ]
.factory 'bindingHistoryService', ['$resource',
    ($resource) ->
      $resource '/api/v3/binding_history/:phone',
        {
          phone: '@phone'
        }
  ]