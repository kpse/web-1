'use strict'

tokenService = ($http) ->
  $http({method: 'GET', url: '/ws/fileToken?bucket=kulebao-prod'})


qiniuService = (tokenService) ->
  send: (file, token, successCallback) ->
    data = new FormData()
    xhr = new XMLHttpRequest()

    xhr.onloadend = (e) ->
      response = JSON.parse(e.currentTarget.response)
      successCallback({
        url: "https://dn-kulebao.qbox.me/" + response.hash
        size: response.size
      })

    # Send to server, where we can then access it with $_FILES['file].
    data.append "file", file
    data.append "token", token
    xhr.open "POST", "http://up.qiniu.com"
    xhr.send data

uploadService = (qiniuService, $http) ->
  (file, callback) ->
    return callback(undefined) if file is undefined
    $http.get('/ws/fileToken?bucket=kulebao-prod').success (data)->
      qiniuService.send file, data.token, (remoteFile) ->
        callback(remoteFile.url)

angular.module('kulebaoAdmin')
.factory 'tokenService', ['$http', tokenService]
angular.module('kulebaoAdmin')
.factory 'qiniuService', ['tokenService', qiniuService]
angular.module('kulebaoAdmin')
.factory 'uploadService', ['qiniuService', '$http', uploadService]


angular.module('kulebaoAdmin').directive "fileupload", ->
  link: (scope, element, attributes) ->
    element.bind "change", (e) ->
      scope.$apply ->
        scope[attributes.fileupload] = e.target.files[0]
        scope.app.file_size = e.target.files[0].size if scope.app isnt undefined
