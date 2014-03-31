angular.module('kulebaoOp').controller 'OpPhoneManagementCtrl',
  ['$scope', '$rootScope', '$location', '$stateParams', 'phoneManageService', '$alert',
    (scope, rootScope, location, stateParams, Phone, Alert) ->
      rootScope.tabName = 'phone_management'


      scope.query = (phone) ->
        scope.parent = Phone.get phone: phone, ->
          scope.navigateTo(phone)
        , (res) ->
          Alert
            title: '未找到手机号' + phone
            content: '请修改条件进行查找。'
            placement: "top-left"
            type: "danger"
            container: '.well'
            duration: 3

      scope.navigateTo = (phone)->
        if (location.path().indexOf('/phone/') < 0)
          location.path(location.path() + '/phone/' + phone)
        else
          location.path(location.path().replace(/\/phone\/.+$/, '') + '/phone/' + phone)

  ]

angular.module('kulebaoOp').controller 'OpShowPhoneCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'phoneManageService', 'schoolService', '$alert',
    (scope, rootScope, stateParams, location, Phone, School, Alert) ->
      scope.parent = Phone.get phone: stateParams.phone, ->
        scope.school = School.get school_id: scope.parent.school_id

      scope.cancel = ->
        location.path(location.path().replace(/\/phone\/.+$/, ''))

      scope.delete = (parent)->
        Phone.delete parent, ->
          Alert
            title: '删除成功'
            content: '手机号为' + parent.phone + '的家长已经删除。'
            placement: "top-left"
            type: "danger"
            container: '.panel-body'
            duration: 3
          scope.cancel()



  ]

