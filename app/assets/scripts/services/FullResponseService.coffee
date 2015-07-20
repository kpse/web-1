'use strict'

angular.module('kulebao.services')
.factory 'fullResponseService', ['$q',  ($q) ->
  fullResponse = (Resource, dic, result=[]) ->
    most = dic.most || 25
    $q (resolve, reject) ->
      Resource.query dic, (data) ->
        if data.length == most
          fullResponse(Resource, (_.assign dic, to: _.last(data).id - 1), result.concat(data)).then (d) ->
            resolve d
          , (err) -> reject err
        else
          resolve result.concat(data)
      , (err) -> reject err
]