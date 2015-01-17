angular.module('kulebaoOp').controller 'OpChargeCtrl',
  ['$scope', '$rootScope', '$location', 'schoolService', 'classService', '$modal', 'chargeService',
   'videoProviderService',
    (scope, rootScope, $location, School, Clazz, Modal, Charge, VideoProvider) ->
      scope.refresh = ->
        scope.kindergartens = School.query ->
          _.each scope.kindergartens, (kg) ->
            kg.charge = Charge.query school_id: kg.school_id
            kg.videoProvider = VideoProvider.get school_id: kg.school_id

      scope.refresh()

      rootScope.tabName = 'charge'

      scope.edit = (kg) ->
        scope.charge = angular.copy kg.charge[0]
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
        charge.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.videoProviderURL = (token) ->
        "#{$location.protocol()}://#{$location.host()}:#{$location.port()}/api/v1/video_member?token=#{token}"

      scope.enableVideo = (schoolId) ->
        VideoProvider.save school_id: schoolId, ->
          scope.kg.videoProvider = VideoProvider.get school_id: schoolId

  ]

