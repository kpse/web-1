'use strict'

imageCompressService = ->
  compress : (url, width, height) ->
    if !url
      ""
    else if width
      url + '?' + 'imageView/2/w/' + width + '/q/50'
    else if height
      url + '?' + 'imageView/2/h/' + height + '/q/50'
    else
      url


angular.module('kulebaoAdmin')
.factory 'imageCompressService', [imageCompressService]
