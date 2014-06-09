'use strict'

eventStreamService = ->
  eventStreamServiceCreated = {}
  init : (url, callback) ->
    old = eventStreamServiceCreated[url]
    if old?
      source = old
      source.addEventListener 'message', callback, false
    else if callback?
      source = new EventSource url
      source.addEventListener 'message', callback, false
      eventStreamServiceCreated[url] = source



angular.module('kulebaoAdmin')
.service('eventStreamService', eventStreamService)