'use strict'

angular.module('kulebaoApp')
.factory 'employeePhoneService', ['$resource',
  ($resource) ->
    $resource '/employee_password/:phone',
      {
        phone: '@phone'
      }
]
