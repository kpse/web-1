angular.module('kulebaoOp').controller 'OpChargeCtrl',
  ['$scope', '$rootScope', '$location', '$filter','schoolService', 'classService', '$modal', 'chargeService',
   'videoProviderService',
    (scope, rootScope, $location, $filter, School, Clazz, Modal, Charge, VideoProvider) ->
      scope.refresh = ->
        scope.kindergartens = School.query ->
          _.each scope.kindergartens, (kg) ->
            Charge.query school_id: kg.school_id, (data) ->
              kg.charge = data[0]
              kg.charge.expire = new Date(data[0].expire_date)
            kg.videoProvider = VideoProvider.get school_id: kg.school_id

      scope.refresh()

      rootScope.tabName = 'charge'

      scope.edit = (kg) ->
        scope.charge = angular.copy kg.charge
        scope.kg = kg
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/update_charge_info.html'


      scope.disable = (charge) ->
        charge.$delete ->
          scope.refresh()

      scope.enable = (charge) ->
        charge.status = 1
        charge.$save ->
          scope.refresh()

      scope.save = (charge) ->
        charge.expire_date = $filter('date')(charge.expire, 'yyyy-MM-dd', '+0800')
        charge.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.videoProviderURL = (token) ->
        "#{$location.protocol()}://#{$location.host()}:#{$location.port()}/api/v1/video_member?token=#{token}"

      scope.enableVideo = (schoolId) ->
        VideoProvider.save school_id: schoolId, ->
          scope.kg.videoProvider = VideoProvider.get school_id: schoolId
          scope.charge.total_video_account = 1

  ]

