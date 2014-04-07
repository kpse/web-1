'use strict'

angular.module('kulebaoAdmin')
.controller 'IntroCtrl', [ '$scope', '$rootScope', '$stateParams',
                           '$location', 'schoolService', '$http', 'uploadService', '$timeout', '$cacheFactory', 'employeeService'
  (scope, rootScope, stateParams, location, School, $http, Upload, $timeout, $cacheFactory, Employee) ->
    rootScope.tabName = 'intro'

    scope.adminUser = Employee.get ->
      scope.kindergarten = School.get school_id: stateParams.kindergarten, ->
        scope.$watch 'kindergarten', (oldv, newv) ->
          scope.school_changed = true if newv isnt oldv
        , true

    scope.school_changed = false
    scope.isEditing = false


    scope.toggleEditing = (e) ->
      e.stopPropagation()
      scope.isEditing = !scope.isEditing
      console.log 'scope.kindergarten changed: ' + scope.school_changed
      if scope.school_changed
        $cacheFactory.get('$http').removeAll();
        School.save scope.kindergarten, ->
          scope.school_changed = false

    scope.uploadPic = (pic) ->
      scope.uploading = true
      Upload pic, (url) ->
        scope.$apply ->
          scope.kindergarten.school_logo_url = url if url isnt undefined
          scope.uploading = false
      , scope.adminUser.id

]
