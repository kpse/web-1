'use strict'

eventStreamService = ->
  eventStreamServiceCreated = {}
  init: (url, callback) ->
    old = eventStreamServiceCreated[url]
    if old? && old.readyState != 1
      old.close()
      delete eventStreamServiceCreated[url]
      old = undefined
    if !old? && callback?
      source = new EventSource url
      source.addEventListener 'message', callback, false
      source.onerror = (e)->
        console.log(e);
        source.close()
      eventStreamServiceCreated[url] = source


angular.module('kulebaoAdmin')
.service('eventStreamService', eventStreamService)