'use strict'

angular.module('kulebao.services')
.factory 'chargeService', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id/charge',
    {
      school_id: '@school_id'
    }

]

angular.module('kulebao.services')
.factory 'activeCountService', ['$resource', ($resource) ->
  $resource '/kindergarten/:school_id/active',
    {
      school_id: '@school_id'
    }
]