angular.module('kulebaoOp').controller 'OpVerificationCtrl',
  ['$scope', '$rootScope', '$resource', '$alert', 'videoMemberService',
    (scope, rootScope, $resource, Alert, DefaultVideo) ->
      rootScope.tabName = 'code'
      Code = $resource '/cheatCode'

      rootScope.loading = true
      scope.code = Code.get ->
        rootScope.loading = false

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

      # video part

      scope.video = DefaultVideo.get school_id: 0, id: 'default', ->
        rootScope.loading = false

      scope.editAccount = (video) ->
        scope.editingAccount = true

      scope.saveAccount = (video) ->
        video.$save ->
            scope.editingAccount = false
          , (res) ->
            Alert
              title: ''
              content: res.data.error_msg
  ]

