'use strict'

angular.module('kulebaoAdmin')
.controller 'IntroCtrl', [ '$scope', '$rootScope', '$stateParams',
                           '$location', 'schoolService', '$http', 'uploadService', '$timeout', '$cacheFactory',
  (scope, rootScope, stateParams, location, School, $http, Upload, $timeout, $cacheFactory) ->
    scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
      scope.school = scope.kindergarten.school_info

      scope.$watch 'kindergarten', (oldv, newv) ->
          scope.school_changed = true if newv isnt oldv
        , true

    scope.school_changed = false
    scope.isEditing = false

    rootScope.tabName = 'intro'
    scope.toggleEditing = (e) ->
      e.stopPropagation()
      scope.isEditing = !scope.isEditing
      console.log 'scope.school changed: ' + scope.school_changed
      if scope.school_changed
        $cacheFactory.get('$http').removeAll();
        $timeout ->
            School.save scope.kindergarten.school_info, -> scope.school = scope.kindergarten.school_info
            scope.school_changed = false
          , 0, true

    scope.uploadPic = (pic) ->
      Upload pic, (url) ->
        scope.$apply ->
          scope.school.school_logo_url = url if url isnt undefined

]
