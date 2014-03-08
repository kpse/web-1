'use strict'

angular.module('kulebaoAdmin')
.factory 'employeeService', ['$resource', 'session',
    ($resource, Session) ->
      $resource '/employee/:phone',
        {
          phone: Session.phone
        }, {
          all: params: phone: null
        }
    ]

angular.module('kulebaoAdmin')
.factory 'allEmployeesService', ['$resource',
    ($resource) ->
      $resource '/employee/:phone',
        {
          phone: @phone
        }
  ]
