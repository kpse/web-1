angular.module('kulebaoOp').controller 'OpChatCtrl',
  ['$scope', '$rootScope', '$timeout', '$location', '$anchorScroll',
    (scope, rootScope, $timeout, $location, $anchorScroll) ->
      rootScope.tabName = 'chat'

      scope.gotoBottom = ->
        $location.hash('bottom');
        $anchorScroll()

      protocol = ->
        if $location.host() == 'localhost'
          "ws://"
        else
          "wss://"

      scope.all = []
      WS = if window['MozWebSocket'] then MozWebSocket else WebSocket
      scope.username = scope.adminUser.name + new Date().getMilliseconds()
      url = "#{protocol()}#{$location.host()}:#{$location.port()}/api/v1/chat_client?username=#{scope.username}"
      console.log(url)
      scope.chatSocket = new WS(url)
      scope.chatSocket.onmessage = scope.receiveEvent

      scope.sendMessage = ->
        scope.chatSocket.send JSON.stringify text: scope.newMessage
        scope.newMessage = ''


      scope.receiveEvent = (event) ->
        data = JSON.parse(event.data)

        if(data.error)
          scope.chatSocket.close()
          alert data.error

        scope.$apply ->
          displayName = if data.user == scope.username then '我' else data.user
          scope.all.push user: displayName, message: data.message, kind: data.kind
          scope.members = _.map data.members, (m) -> if m == scope.username then '我(' + m + ')' else m

        scope.gotoBottom()

      scope.leave = ->
        scope.all.push user: scope.username, message: '你已经退出聊天。'
        scope.members = []
        scope.chatSocket.close()
        scope.chatSocket = undefined
        $location.path '/main/school'

      scope.isAction = (message) ->
        message.kind != 'talk'

      scope.keyPress = (event) ->
        if (event.which == 13)
          event.preventDefault()
          scope.sendMessage()

  ]
