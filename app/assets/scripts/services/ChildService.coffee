'use strict'

angular.module('kulebao.services')
.factory('childService', ['$resource', ($resource) ->
    $resource '/kindergarten/:school_id/child/:child_id',
      {
        school_id: '@school_id'
        child_id: '@child_id'
      }
  ]
)
.factory('childNameCheckService', ['$resource', ($resource) ->
    $resource '/api/v1/kindergarten/:school_id/child_name_check',
      {
        school_id: '@school_id'
        name: '@name'
      }, {
        check:
          method: 'POST'
      }
  ]
)

