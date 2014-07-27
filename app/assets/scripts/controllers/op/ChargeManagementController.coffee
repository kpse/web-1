angular.module('kulebaoOp').controller 'OpChargeCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', '$modal', 'chargeService',
    (scope, rootScope, School, Clazz, Modal, Charge) ->

      scope.refresh = ->
        scope.kindergartens = School.query ->
          _.each scope.kindergartens, (kg) ->
            kg.charge = Charge.query(school_id: kg.school_id)


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
        charge.status=1
        charge.$save ->
          scope.refresh()

      scope.save = (charge) ->
        charge.$save ->
          scope.refresh()
          scope.currentModal.hide()
  ]

