'use strict'

angular.module("kulebao.directives").directive "klFileUpload",
  ['multipleUploadService',
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
          images: "=?multiple"

        link: (scope, element, attrs) ->
          scope.label = scope.label || '上传'
          scope.controlDisabled = scope.controlDisabled || false
          scope.field = scope.fieldName || 'image'
          scope.suffixes = scope.suffix || 'jpg|png'
          scope.regex = new RegExp("\.(#{scope.suffixes})$", 'i')
          scope.uploading = false
          scope.fileControl = element[0].firstChild
          scope.suffixError = false
          scope.limitationError = false
          scope.images = scope.images || []
          scope.fileControl.onchange = (e) ->
            targetFiles = e.target.files
            if (targetFiles.length + scope.images.length) > 9
              scope.$apply ->
                scope.limitationError = true
            else if !scope.validateFiles(targetFiles)
              scope.$apply ->
                scope.suffixError = true
            else
              scope.$apply ->
                scope.targetFiles = e.target.files
                scope.targetFile = scope.targetFiles[0]
                scope.fileSize = scope.targetFile.size if scope.targetFile?
                scope.suffixError = false
                scope.limitationError = false

          scope.validateFiles = (files) ->
            _.every files, (f) -> f.name.match(scope.regex)?

          scope.uploadPic = ->
            scope.uploading = true
            Upload scope.targetFiles, scope.user, scope.combineSuccess(scope.onSuccess), scope.combineFailure(scope.onError)

          scope.cleanUp = ->
            scope.$apply ->
              scope.uploading = false
              delete scope.targetFile
              delete scope.targetFiles
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

        templateUrl: (elem, attr) ->
          if attr.multiple?
            'templates/directives/kl_upload_multiple_files.html'
          else
            'templates/directives/kl_upload_file.html'
      )
  ]