'use strict'

describe 'Directive: klFileUpload', () ->

  mockUploadServiceFlag = true
  mockUploadService = ->
    (file, user, onSuccess, onError) ->
      if mockUploadServiceFlag
        onSuccess(url: 'some_url', size: 100)
      else
        onError(error: 'some_error')


  # load the directive's module
  beforeEach ->
    angular.module('mock', []).factory 'uploadService', mockUploadService
    angular.module 'test', ['kulebaoAdmin', 'mock']
    module 'test'

  scope = {}

  beforeEach inject ($controller, $rootScope, $templateCache) ->
    scope = $rootScope.$new()
    scope.user =
      id: 'user_id'
    $templateCache.put 'templates/directives/kl_upload_file.html', '<input type="file" class="picture-input" ng-disabled="controlDisabled"/>
              <button class="btn btn-success btn-xs"
                      ng-disabled="targetFile === undefined || targetFile.size > 2048000 || uploading || controlDisabled"
                      ng-click="uploadPic()">{{label}}
              </button>
              <span ng-class="{error : targetFile.size > 2048000}" class="help-block">图片大小请不要超过2m</span>
              <div class="uploading" ng-show="uploading"></div>'

  afterEach ->
    scope.$destroy()

  it 'should report error while uploading fail', inject ($compile) ->
    mockUploadServiceFlag = false
    scope.stubFailure = (res) ->
      scope.error = res.error

    scope.targetFile = 'filename'
    element = angular.element '<kl-file-upload user="user" on-error="stubFailure"></kl-file-upload>'
    element = $compile(element)(scope)
    scope.$digest()

    angular.element(element[0].children[1]).triggerHandler('click')
    expect(scope.error).toBe 'some_error'

    #TODO: figure out how to avoid the apply in prog error
#  it 'should display DOMs when using element form', inject ($compile) ->
#    mockUploadServiceFlag = true
#
#    scope.stubSuccess = (url, size) ->
#      scope.url = url
#      scope.size = size
#
#    scope.targetFile = 'filename'
#    element = angular.element '<kl-file-upload user="user" on-success="stubSuccess"></kl-file-upload>'
#    element = $compile(element)(scope)
#    scope.$digest()
#
#    angular.element(element[0].children[1]).triggerHandler('click')
#    expect(scope.url).toBe 'some_url'
#    expect(scope.size).toBe 100


#  it 'should display DOMs when using attribute form', inject ($compile) ->
#    expect(scope.url).toBe 'some_url'
#
#
#  it 'should pass out ng-model after uploading successfully', inject ($compile) ->
#    expect(scope.url).toBe 'some_url'