'use strict'

angular.module('kulebao.services')
.factory 'assessService', ['$resource',
  ($resource) ->
    $resource '/kindergarten/:school_id/child/:child_id/assess/:assess_id',
      {
        school_id: '@school_id'
        child_id: '@child_id'
        assess_id: '@id'
      }

]