'use strict'

angular.module('kulebaoAdmin')
.controller 'InteractionController',
  [ '$scope', '$rootScope', '$stateParams', '$modal',
    (scope, rootScope, stateParams, Modal) ->
      rootScope.tabName = 'interaction'
      scope.heading = '配置家园互动关键字'

      rootScope.loading = false

      scope.keywords = [{id: 1, word: '老师'}, {id: 2, word: '家长'}, {id: 3, word: '吃药'}, {id: 4, word: '打架'}]

      scope.addKeyword = ->
        scope.newAddingWords = []
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_keyword.html'

      scope.saveKeyword = (keyword) ->

      scope.deleteKeyword = (keyword) ->

      scope.multipleDelete = ->
  ]
