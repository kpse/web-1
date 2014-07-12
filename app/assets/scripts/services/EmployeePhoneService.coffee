'use strict'

angular.module('kulebao.services')
.factory 'employeePhoneService', ['$resource',
  ($resource) ->
    $resource '/employee_password/:phone',
      {
        phone: '@phone'
      }
]
