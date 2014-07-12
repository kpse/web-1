'use strict'

angular.module('kulebao.services')
.factory 'employeeService', ['$resource', 'session',
    ($resource, Session) ->
      $resource '/employee/:phone',
        {
          phone: Session.phone
        }, {
          all:
            params:
              phone: '@phone'
        }
  ]

angular.module('kulebao.services')
.factory 'allEmployeesService', ['$resource',
    ($resource) ->
      $resource '/employee/:phone',
        {
          phone: '@phone'
        }
  ]

angular.module('kulebao.services')
.factory 'schoolEmployeesService', ['$resource',
    ($resource) ->
      $resource '/kindergarten/:school_id/employee/:phone',
        {
          school_id: '@school_id'
          phone: '@phone'
        }
  ]

angular.module('kulebao.services')
.factory 'employeesManageClassService', ['$resource',
    ($resource) ->
      $resource '/kindergarten/:school_id/employee/:phone/class',
        {
          school_id: '@school_id'
          phone: '@phone'
        }
  ]