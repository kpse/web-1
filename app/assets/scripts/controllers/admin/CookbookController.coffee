'use strict'

angular.module('kulebaoAdmin')
.controller 'CookbookCtrl', [ '$scope', '$rootScope', '$stateParams',
                              'schoolService', 'cookbookService',
  (scope, rootScope, stateParams, School, Cookbook) ->
    rootScope.tabName = 'cookbook'
    scope.cookbook_changed = false
    scope.isEditing = false

    scope.kindergarten = School.get school_id: stateParams.kindergarten

    scope.cookbooks = Cookbook.bind(school_id: stateParams.kindergarten).query ->
      scope.cookbook = scope.cookbooks[0]
      if !scope.cookbook?
        scope.cookbook = new Cookbook
          school_id: parseInt stateParams.kindergarten
      scope.$watch 'cookbook', (oldv, newv) ->
        scope.cookbook_changed = true if newv isnt oldv
      , true

    scope.toggleEditing = (e) ->
      e.stopPropagation()
      scope.isEditing = !scope.isEditing
      console.log 'scope.cookbook changed: ' + scope.cookbook_changed
      if scope.cookbook_changed
        scope.cookbook.$save()
        scope.cookbook_changed = false
]
