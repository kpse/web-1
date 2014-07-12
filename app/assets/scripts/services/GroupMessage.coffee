'use strict'

angular.module('kulebao.services')
  .service 'GroupMessage', ['$resource', ($resource) ->
    $resource('/ws/broadcast')
  ]