'use strict'

angular.module('kulebaoAdmin')
.controller 'InteractionController',
  [ '$scope', '$rootScope', '$stateParams', '$q', '$modal', 'keywordService',
    (scope, rootScope, stateParams, $q, Modal, Keyword) ->
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
        allNewAdded = _.map _.uniq keywords.split("\n"), (k) ->
          newKeyword = new Keyword(word: k)
          newKeyword.$save(school_id: stateParams.kindergarten).$promise
        $q.all(allNewAdded).then ->
          scope.currentModal.hide()
          scope.refresh()

      scope.deleteKeyword = (keyword) ->
        Keyword.remove school_id: stateParams.kindergarten, id: keyword.id, ->
          scope.refresh()

      scope.multipleDelete = ->


  ]
