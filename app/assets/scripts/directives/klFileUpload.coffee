'use strict'

angular.module("kulebao.directives").directive "klFileUpload",
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
          controlDisabled: "=?disabled"
          label: "=?label"
          form: "=?"
          suffix: "@?"

        link: (scope, element, attrs) ->
          scope.label = scope.label || '上传'
          scope.controlDisabled = scope.controlDisabled || false
          scope.field = scope.fieldName || 'image'
          scope.suffixes = scope.suffix || 'jpg|png'
          scope.regex = new RegExp("\.(#{scope.suffixes})$", 'i')
          scope.uploading = false
          scope.fileControl = element[0].firstChild
          scope.suffixError = false
          scope.fileControl.onchange = (e) ->
            unless e.target.files[0].name.match(scope.regex)?
              scope.$apply ->
                scope.suffixError = true
            else
              scope.$apply ->
                scope.targetFile = e.target.files[0]
                scope.fileSize = e.target.files[0].size if scope.targetFile?
                scope.suffixError = false

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
                console.log "上传失败，错误：#{res.error}"

          scope.combineSuccess = (f) ->
            (url, size, other)->
              f(url, size, other) if angular.isFunction(f)
              if scope.ngModel
                scope.$apply ->
                  scope.ngModel[scope.field] = url
                  scope.ngModel[scope.field + 'Size'] = size
                  scope.form.$setDirty() if scope.form?
              scope.cleanUp()

        templateUrl: 'templates/directives/kl_upload_file.html'
      )
  ]