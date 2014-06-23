'use strict'


feedbackService= ($resource) ->
  $resource '/feedback/:feedback_id', {
    feedback_id: '@id'
  }

angular.module('kulebaoOp')
.factory('feedbackService', ['$resource', feedbackService])
