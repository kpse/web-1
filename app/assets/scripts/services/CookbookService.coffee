'use strict'

cookbookService = ($resource) ->
  $resource '/kindergarten/:school_id/cookbook/:cookbook_id',
    {
      school_id: '@school_id'
      cookbook_id: '@cookbook_id'
    }

angular.module('kulebao.services')
.factory('cookbookService', ['$resource', cookbookService])