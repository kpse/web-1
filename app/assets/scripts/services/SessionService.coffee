'use strict'

sessionService = ($cookies) ->
  # read Play session cookie
  rawCookie = $cookies["PLAY_SESSION"]
  rawData = rawCookie.substring(rawCookie.indexOf("-") + 1, rawCookie.length - 1)
  session = {}
  _.each rawData.split("&"), (rawPair) ->
    pair = rawPair.split("=")
    session[pair[0]] = pair[1]
  session


angular.module('kulebao.services')
.factory('session', ['$cookies', sessionService])