'use strict'

assignmentService = ($resource) ->
  $resource '/kindergarten/:school_id/assignment/:id',
    {
      school_id: '@school_id'
      id: '@id'
    }

angular.module('kulebaoAdmin')
.factory('assignmentService', ['$resource', assignmentService])