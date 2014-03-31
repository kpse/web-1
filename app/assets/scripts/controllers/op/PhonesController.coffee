angular.module('kulebaoOp').controller 'OpPhoneManagementCtrl',
  ['$scope', '$rootScope', '$location', 'phoneManageService', 'parentService',
    (scope, rootScope, location, Phone, Parent) ->
      rootScope.tabName = 'phone_management'

      scope.deletePhone = (phone) ->
        Phone.delete phone: phone

      scope.query = (phone) ->
        if (location.path().indexOf('/phone/') < 0)
          location.path(location.path() + '/phone/' + phone)
        else
          location.path(location.path().replace(/\/phone\/.+$/, '') + '/phone/' + phone)

  ]

angular.module('kulebaoOp').controller 'OpShowPhoneCtrl',
  ['$scope', '$rootScope', '$stateParams', 'phoneManageService',
    (scope, rootScope, stateParams, Phone) ->
      scope.deletePhone = (phone) ->
        Phone.delete phone: phone

      scope.parent = Phone.get phone: stateParams.phone



  ]

