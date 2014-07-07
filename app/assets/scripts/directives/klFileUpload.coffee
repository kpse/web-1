'use strict'

angular.module("kulebaoAdmin").directive "klFileUpload",
  ['uploadService',
    (Upload) ->
      return (
        restrict: "EA"
        scope:
          ngModel: "=ngModel"
          fieldName: "@fieldName"
          user: "="
          onSuccess: "=onSuccess"
          onError: "=onError"
          controlDisabled: "@disabled"
          label: "@"

        link: (scope, element) ->
          scope.buttonLabel = scope.label || '上传'
          scope.fieldName = scope.fieldName || 'image'
          scope.uploading = false
          scope.fileControl = element[0].firstChild
          scope.fileControl.onchange = (e) ->
            scope.$apply ->
              scope.targetFile = e.target.files[0]
              scope.fileSize = e.target.files[0].size if scope.targetFile?

          scope.uploadPic = ->
            scope.uploading = true
            Upload scope.targetFile, scope.user, scope.combineSuccess(scope.onSuccess), scope.combineFailure(scope.onError)

          scope.cleanUp = ->
            scope.$apply ->
              scope.uploading = false
              delete scope.targetFile
              angular.element(scope.fileControl).val(null)

          scope.combineFailure = (f) ->
            (res, other)->
              if angular.isFunction(f)
                f(res, other)
              else
                console.log '上传失败，错误e=' + res.error

          scope.combineSuccess = (f) ->
            (url, size, other)->
              f(url, size, other) if angular.isFunction(f)
              if scope.ngModel
                scope.$apply ->
                  scope.ngModel[scope.fieldName] = url
                  scope.ngModel[scope.fieldName + 'Size'] = size
              scope.cleanUp()

        templateUrl: 'templates/directives/kl_upload_file.html'
      )
  ]