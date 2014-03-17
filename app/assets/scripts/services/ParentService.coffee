'use strict'

parentService = ($resource) ->
  $resource '/kindergarten/:school_id/parent/:phone',
    {
      school_id: '@school_id'
      phone: '@phone'
    }, {
      members:
        method: 'GET'
        params:
          member: true
        isArray:true
      nonMembers:
        method: 'GET'
        params:
          member: false
        isArray:true
    }

angular.module('kulebaoAdmin')
.factory('parentService', ['$resource', parentService])