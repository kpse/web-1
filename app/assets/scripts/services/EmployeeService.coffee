'use strict'

angular.module('kulebao.services')
.factory('employeeService', ['$resource', 'session',
    ($resource, Session) ->
      $resource '/employee/:phone',
        {
          phone: Session.phone
        }, {
          all:
            params:
              phone: '@phone'
          get:
            method: 'GET'
            cache: true
        }
  ]
)
.factory('allEmployeesService', ['$resource',
    ($resource) ->
      $resource '/employee/:phone',
        {
          phone: '@phone'
        }
  ]
)
.factory('schoolEmployeesService', ['$resource',
    ($resource) ->
      $resource '/kindergarten/:school_id/employee/:phone',
        {
          school_id: '@school_id'
          phone: '@phone'
        }
  ]
)
.factory('employeesManageClassService', ['$resource',
    ($resource) ->
      $resource '/kindergarten/:school_id/employee/:phone/class',
        {
          school_id: '@school_id'
          phone: '@phone'
        }
  ]
)
.factory('eligibleCheckService', ['$resource', ($resource) ->
    $resource '/api/v3/kindergarten/:school_id/employee/:id/ineligible_class'
  ]
)