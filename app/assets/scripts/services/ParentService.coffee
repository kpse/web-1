'use strict'

angular.module('kulebao.services')
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


angular.module('kulebao.services')
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
        employee: '@employee'
      }, {
        'check':
          method: 'POST'
          cache: true
      }
]

angular.module('kulebao.services')
.factory 'phoneCheckInSchoolService', ['$resource',
  ($resource) ->
    $resource 'api/v1/kindergarten/:school_id/phone_check/:phone',
      {
        school_id: '@school_id'
        phone: '@phone'
        employee: '@employee'
      }, {
        'check':
          method: 'POST'
          cache: true
      }
]

angular.module('kulebao.services')
.factory 'parentV3Service', ['$resource',
  ($resource) ->
    $resource '/api/v3/kindergarten/:school_id/relative/:id',
      {
        school_id: '@school_id'
        id: '@id'
      }
]