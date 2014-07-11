'use strict'

angular.module('kulebaoAdmin')
.factory 'parentService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/parent/:phone',
      {
        school_id: '@school_id'
        phone: '@phone'
      }, {
        members:
          method: 'GET'
          params:
            member: true
          isArray: true
        nonMembers:
          method: 'GET'
          params:
            member: false
          isArray: true
      }
]


angular.module('kulebaoAdmin')
.factory 'phoneManageService', ['$resource',
  ($resource) ->
    $resource '/phone/:phone',
      {
        phone: '@phone'
      }
]

angular.module('kulebao.services')
.factory 'phoneCheckService', ['$resource',
  ($resource) ->
    $resource 'api/v1/phone_check/:phone',
      {
        phone: '@phone'
      }, {
        'check': method: 'POST'
      }
]