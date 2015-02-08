angular.module('kulebaoOp').controller 'OpFeedbackCtrl',
  ['$scope', '$rootScope', '$state', '$stateParams',
    (scope, rootScope, $state, stateParams) ->
      rootScope.tabName = 'feedback'

      scope.sources = [
        {name: '安卓家长', type: 'android_parent'}
        {name: '安卓教师', type: 'android_teacher'}
        {name: '苹果家长', type: 'ios_parent'}
        {name: '苹果教师', type: 'ios_teacher'}
        {name: '已处理', type: 'done'}
      ]

      $state.go('main.feedback.source.list', source: scope.sources[0].type) unless stateParams.source?

  ]

.controller 'OpFeedbackSourceCtrl',
  ['$scope', '$rootScope', '$location', '$stateParams', 'feedbackService',
    (scope, rootScope, location, stateParams, Feedback) ->

      scope.refresh = ->
        scope.loading = true
        scope.allFeedback = Feedback.query source: stateParams.source, ->
          scope.loading = false

      scope.refresh()

      scope.close = (feedback)->
        feedback.source = 'done'
        feedback.$save ->
          scope.refresh()

  ]



