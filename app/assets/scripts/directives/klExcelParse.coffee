'use strict'

angular.module("kulebao.directives").directive "klExcelParse", ->
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
      scope.label = scope.label || '读入数据'
      scope.controlDisabled = scope.controlDisabled || false
      scope.field = scope.fieldName || 'image'
      scope.suffixes = scope.suffix || 'xls|xlsx'
      scope.regex = new RegExp("\.(#{scope.suffixes})$", 'i')
      scope.uploading = false
      scope.fileControl = element[0].firstChild
      scope.suffixError = false
      scope.limitationError = false
      scope.images = scope.images || []
      scope.excelUtil = XLS
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
            scope.excelUtil = if stringEndsWith scope.targetFile.name, '.xls' then XLS else XLSX

      stringEndsWith = (name, s) -> s is '' or name[-s.length..] is s

      scope.validateFiles = (files) ->
        _.every files, (f) ->
          f.name.match(scope.regex)?

      toJson = (workbook) ->
        _.reduce workbook.SheetNames, (all, sheetName) ->
            roa = scope.excelUtil.utils.sheet_to_row_object_array(workbook.Sheets[sheetName])
            all[sheetName] = roa  if roa.length > 0
            all
          , {};

      scope.parseFile = ->
        scope.uploading = true
        reader = new FileReader();
        reader.onload = (e) ->
          data = e.target.result;
          wb = scope.excelUtil.read(data, {type: 'binary'});
          scope.ngModel = toJson(wb)
          scope.combineSuccess(scope.onSuccess)(scope.ngModel)
          scope.cleanUp()
        reader.readAsBinaryString(scope.targetFile)

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
        (data)->
          f(data) if angular.isFunction(f)
          if scope.ngModel
            scope.$apply ->
              scope.ngModel = data
              scope.form.$setDirty() if scope.form?
          scope.cleanUp()

    templateUrl: 'templates/directives/kl_input_excel_file.html'
  )