'use strict'


feedbackService= ($resource) ->
  $resource '/feedback/:feedback_id', {
    feedback_id: '@id'
  }

angular.module('kulebao.services')
.factory('feedbackService', ['$resource', feedbackService])
