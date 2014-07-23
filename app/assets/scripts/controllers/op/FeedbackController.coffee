angular.module('kulebaoOp').controller 'OpFeedbackCtrl',
  ['$scope', '$rootScope', 'feedbackService', '$location', '$stateParams', '$timeout',
    (scope, rootScope, Feedback, location, stateParams, $timeout) ->
      rootScope.tabName = 'feedback'

      scope.sources = [
        {name: '安卓家长', type: 'android_parent'}
        {name: '安卓教师', type: 'android_teacher'}
        {name: '苹果家长', type: 'ios_parent'}
        {name: '苹果教师', type: 'ios_teacher'}
        {name: '已处理', type: 'done'}
      ]


      scope.navigateTo = (source) ->
        location.path("/main/feedback/source/#{source.type}/list") if stateParams.source != source.type

      if stateParams.source is undefined
        scope.navigateTo scope.sources[0]

      scope.current_source = stateParams.source
  ]

.controller 'OpFeedbackSourceCtrl',
  ['$scope', '$rootScope', 'feedbackService', '$location', '$stateParams',
    (scope, rootScope, Feedback, location, stateParams) ->
      scope.$emit 'set_tab', stateParams.source

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



