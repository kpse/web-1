'use strict'

angular.module("kulebaoAdmin").directive "klFileUpload",
  ['uploadService',
    (Upload) ->
      return (
        restrict: "EA"
        scope:
          user: "="
          onSuccess: "=onSuccess"
          onError: "=onError"
          controlDisabled: "@disabled"
          label: "@"

        link: (scope, element) ->
          scope.buttonLabel = scope.label || '上传'
          scope.uploading = false
          scope.fileControl = element[0].firstChild
          scope.fileControl.onchange = (e) ->
            scope.$apply ->
              scope.targetFile = e.target.files[0]
              scope.fileSize = e.target.files[0].size if scope.targetFile?

          scope.uploadPic = ->
            scope.uploading = true
            Upload scope.targetFile, scope.user, scope.combine(scope.onSuccess), scope.combine(scope.onError)

          scope.cleanUp = ->
            scope.$apply ->
              scope.uploading = false
              angular.element(scope.fileControl).val(null)

          scope.combine = (f) ->
            (p1, p2, p3)->
              f(p1, p2, p3) if angular.isFunction(f)
              scope.cleanUp()

        templateUrl: 'templates/directives/kl_upload_file.html'
      )
  ]