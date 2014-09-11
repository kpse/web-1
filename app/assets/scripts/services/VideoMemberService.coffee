'use strict'

angular.module('kulebao.services').factory 'videoMemberService',
  ['$resource',
    ($resource) ->
      $resource '/api/v1/kindergarten/:school_id/video_member/:parent_id', {
          school_id: '@school_id'
          parent_id: '@id'
        }
  ]