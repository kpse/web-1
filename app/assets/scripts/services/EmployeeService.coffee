'use strict'

employeeService = ($resource, Session) ->
  $resource '/employee/:phone',
    {
      phone: Session.phone
    }

angular.module('kulebaoAdmin')
.factory('employeeService', ['$resource', 'session', employeeService])
