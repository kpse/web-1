angular.module('kulebaoOp').controller 'OpVerificationCtrl',
  ['$scope', '$rootScope', '$resource', '$alert',
    (scope, rootScope, $resource, Alert) ->
      rootScope.tabName = 'code'
      Code = $resource '/cheatCode'

      scope.loading = true
      scope.code = Code.get ->
        scope.loading = false

      scope.edit = (code) ->
        scope.editing = true

      scope.save = (code) ->
        code.$save ->
          scope.editing = false
        , (res) ->
          Alert
            title: ''
            content: res.data.error_msg

      scope.delete = (code) ->
        code.$delete ->
          scope.code = ''

  ]

