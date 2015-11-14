'use strict'

postUrl = "https://up.qbox.me"

tokenService = ($http) ->
  token: (file, remoteDir, bucketName) ->
    $http.post '/ws/safe_file_token' ,
      name: bucketName
      key: generateRemoteFileName remoteDir, removeInvalidChars(file.name)

rawFileTokenService = ($http) ->
  token: (file, remoteDir, bucketName) ->
    $http.get "/ws/fileToken?bucket=#{bucketName}&key=#{remoteDir}/#{file.name}"


qiniuService = (tokenService) ->
  send: (file, remoteDir, token, bucketUrl, successCallback, errorCallback) ->
    data = new FormData()
    xhr = new XMLHttpRequest()
    xhr.onloadend = (e) ->
      response = JSON.parse(e.currentTarget.response)
      if (response.error?)
        if errorCallback?
          errorCallback(response)
        else
          console.log(response)
      else
        successCallback({
          url: bucketUrl + generateRemoteFileName remoteDir, encodeURI removeInvalidChars response.name
          size: response.size
        })
      angular.forEach angular.element("input[type='file']"), (elem)->
        angular.element(elem).val(null)

    # Send to server, where we can then access it with $_FILES['file].
    data.append "file", file
    data.append "token", token
    key = generateRemoteFileName remoteDir, removeInvalidChars(file.name)
    data.append "key", key
    xhr.open "POST", postUrl
    xhr.send data

qiniuRawFileService = (tokenService) ->
  send: (file, remoteDir, token, bucketUrl, successCallback) ->
    data = new FormData()
    xhr = new XMLHttpRequest()

    xhr.onloadend = (e) ->
      response = JSON.parse(e.currentTarget.response)
      successCallback({
        url: bucketUrl + remoteDir  + '/' + response.name
        size: response.size
      })
      angular.forEach angular.element("input[type='file']"), (elem)->
        angular.element(elem).val(null)


    # Send to server, where we can then access it with $_FILES['file].
    data.append "file", file
    data.append "token", token
    data.append "key", remoteDir + '/' + file.name
    xhr.open "POST", postUrl
    xhr.send data

generateRemoteDir = (user) -> if user? then user else ''

removeInvalidChars = (name) -> if name? then name.replace(/\s/g, '_') else ''

generateRemoteFileName = (remoteDir, fileName)->
  if remoteDir is ''
    fileName
  else
    remoteDir + '/' + encodeURI fileName


uploadService = (qiniuService, tokenService, bucketInfoService) ->
  (file, user, onSuccess, onError) ->
    return (onError || angular.noop)(error: '没有指定文件。') if file is undefined
    remoteDir = generateRemoteDir user
    bucketInfoService.success (bucket) ->
      tokenService.token(file, remoteDir, bucket.name).success (data)->
        qiniuService.send file, remoteDir, data.token, bucket.urlPrefix, (remoteFile) ->
          (onSuccess || angular.noop)(remoteFile.url)

multipleUploadService = (uploadService) ->
  (files, user, onSuccess, onError) ->
    _.each files, (f) -> uploadService(f, user, onSuccess, onError)

angular.module('kulebao.services')
.factory('bucketInfoService', ['$http', ($http) ->
    $http.get '/ws/bucket_info/default', cache: true
  ]
)
.factory('tokenService', ['$http', tokenService])
.factory('rawFileTokenService', ['$http', rawFileTokenService])
.factory('qiniuService', ['tokenService', qiniuService])
.factory('qiniuRawFileService', ['rawFileTokenService', qiniuRawFileService])
.factory('uploadService', ['qiniuService', 'tokenService', 'bucketInfoService', uploadService])
.factory('appUploadService', ['qiniuRawFileService', 'rawFileTokenService', 'bucketInfoService', uploadService])
.factory('multipleUploadService', ['uploadService', multipleUploadService])


angular.module('kulebao.services').directive "fileupload", ->
  link: (scope, element, attributes) ->
    element.bind "change", (e) ->
      scope.$apply ->
        scope[attributes.fileupload] = e.target.files[0]
        scope.app.file_size = e.target.files[0].size if scope.app isnt undefined
