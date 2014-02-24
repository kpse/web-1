'use strict'

assignmentService = ($resource) ->
  $resource '/kindergarten/:school_id/assignment/:assignment_id',
    {
      school_id: '@school_id'
      assignment_id: '@assignment_id'
    }

angular.module('kulebaoAdmin')
.factory('assignmentService', ['$resource', assignmentService])