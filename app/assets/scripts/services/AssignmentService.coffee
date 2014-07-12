'use strict'

angular.module('kulebao.services')
.factory 'assignmentService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/assignment/:id',
      {
        school_id: '@school_id'
        id: '@id'
      }

]