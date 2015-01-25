'use strict'

angular.module('kulebao.services')
.factory 'employeePhoneService', ['$resource',
  ($resource) ->
    $resource '/employee_password/:phone',
      {
        phone: '@phone'
      }
]

angular.module('kulebao.services')
.factory 'parentPasswordService', ['$resource',
  ($resource) ->
    $resource '/api/v2/admin_password_reset/:phone',
      {
        phone: '@phone'
      }
]
