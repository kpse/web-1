'use strict'

tokenService = ($http) ->
  token: (file, remoteDir) ->
    $http.get '/ws/safe_file_token?bucket=kulebao-prod&key=' + encodeURIComponent remoteDir + file.name


qiniuService = (tokenService) ->
  send: (file, remoteDir, token, successCallback) ->
    data = new FormData()
    xhr = new XMLHttpRequest()

    xhr.onloadend = (e) ->
      response = JSON.parse(e.currentTarget.response)
      successCallback({
        url: "https://dn-kulebao.qbox.me/" + generateRemotefileName remoteDir, response.name
        size: response.size
      })

    # Send to server, where we can then access it with $_FILES['file].
    data.append "file", file
    data.append "token", token
    data.append "key", encodeURIComponent remoteDir + file.name
    xhr.open "POST", "http://up.qiniu.com"
    xhr.send data

generateRemoteDir = (user)->
  return '' if user is undefined
  '/' + user + '/'

generateRemotefileName = (remoteDir, fileName)->
  return fileName if remoteDir is ''
  encodeURIComponent encodeURIComponent remoteDir + fileName

uploadService = (qiniuService, tokenService) ->
  (file, callback, user) ->
    return callback(undefined) if file is undefined
    remoteDir = generateRemoteDir user
    tokenService.token(file, remoteDir).success (data)->
      qiniuService.send file, remoteDir, data.token, (remoteFile) ->
        callback(remoteFile.url)

angular.module('kulebaoAdmin')
.factory 'tokenService', ['$http', tokenService]
angular.module('kulebaoAdmin')
.factory 'qiniuService', ['tokenService', qiniuService]
angular.module('kulebaoAdmin')
.factory 'uploadService', ['qiniuService', 'tokenService', uploadService]


angular.module('kulebaoAdmin').directive "fileupload", ->
  link: (scope, element, attributes) ->
    element.bind "change", (e) ->
      scope.$apply ->
        scope[attributes.fileupload] = e.target.files[0]
        scope.app.file_size = e.target.files[0].size if scope.app isnt undefined
