'use strict'

angular.module('kulebao.services')
.factory('StatsService', ['$resource', ($resource) ->
  (name) ->
    $resource "/api/v1/statistics/#{name}/:school_id",
      {
        school_id: '@school_id'
      }, {
        query: {
          method: "GET"
          isArray:true
          cache: true
        }
      }
]
).factory('StatsServiceV4', ['$resource', ($resource) ->
  (name) ->
    $resource "/api/v4/statistics/#{name}/:school_id",
      {
        school_id: '@school_id'
      }, {
        query: {
          method: "GET"
          isArray:true
          cache: true
        }
        get: {
          cache: true
        }
      }
])


