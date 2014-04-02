'use strict'

angular.module('kulebaoApp')
.factory 'employeePhoneService', ['$resource', 'session',
    ($resource, Session) ->
      $resource '/employee/:phone',
        {
          phone: '@phone'
        }
  ]
