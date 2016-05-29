'use strict'

angular.module('kulebaoAdmin')
.controller 'InteractionController',
  [ '$scope', '$rootScope', '$stateParams', '$modal', 'keywordService',
    (scope, rootScope, stateParams, Modal, Keyword) ->
      rootScope.tabName = 'interaction'
      scope.heading = '配置家园互动关键字'



      scope.refresh = ->
        Keyword.query school_id: stateParams.kindergarten, (data) ->
          scope.keywords = data
          rootScope.loading = false

      scope.refresh()

      scope.addKeyword = ->
        scope.newAddingWords = []
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_keyword.html'

      scope.saveKeyword = (keywords) ->
        scope.currentModal.hide()
        scope.refresh()

      scope.deleteKeyword = (keyword) ->
        Keyword.remove school_id: stateParams.kindergarten, id: keyword.id, ->
          scope.refresh()

      scope.multipleDelete = ->


  ]
