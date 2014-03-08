'use strict'

angular.module('kulebaoAdmin')
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

angular.module('kulebaoAdmin')
.factory 'allEmployeesService', ['$resource',
    ($resource) ->
      $resource '/employee/:phone',
        {
          phone: '@phone'
        }
  ]

angular.module('kulebaoAdmin')
.factory 'schoolEmployeesService', ['$resource',
    ($resource) ->
      $resource '/kindergarten/:school_id/employee/:phone',
        {
          school_id: '@school_id'
          phone: '@phone'
        }
  ]
