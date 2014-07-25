'use strict'

angular.module('kulebaoAdmin')
.controller 'IntroCtrl', [ '$scope', '$rootScope', '$stateParams',
                           'schoolService', '$cacheFactory', 'employeeService'
  (scope, rootScope, stateParams, School, $cacheFactory, Employee) ->
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
        $cacheFactory.get('$http').removeAll()
        School.save scope.kindergarten, ->
          scope.school_changed = false

    scope.onFileUploadSuccess = (url) ->
      scope.$apply ->
        scope.kindergarten.school_logo_url = url if url isnt undefined

    scope.onFileUploadError = (res) ->
      alert '上传失败，错误e=' + res.error

]
