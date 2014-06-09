'use strict'

eventStreamService = ->
  eventStreamServiceCreated = {}
  init: (url, callback) ->
    old = eventStreamServiceCreated[url]
    if old?
      old.close()
      delete eventStreamServiceCreated[url]
      old = undefined
    if callback?
      source = new EventSource url
      source.onmessage = callback
      source.onerror = (e)->
        console.log(e);
        source.close()
      eventStreamServiceCreated[url] = source


angular.module('kulebaoAdmin')
.service('eventStreamService', eventStreamService)