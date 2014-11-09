angular.module('kulebaoOp').controller 'OpAdsCtrl',
  ['$scope', '$rootScope', '$location', 'schoolService', 'adService', '$modal',
    (scope, rootScope, $location, School, Ad, Modal) ->
      rootScope.tabName = 'ad'

      scope.refresh = ->
        scope.loading = true
        scope.kindergartens = School.query (all)->
          _.forEach all, (k) ->
            k.ad = Ad.query school_id: k.school_id
          scope.loading = false

      scope.refresh()

      scope.allAds = ->
        $location.path "main/all_commercials"

      scope.positionReport = (kg) ->
        if kg.ad && kg.ad.length > 0 then _.map(kg.ad , (a) -> a.position_id).join(',') else '无'

      scope.editAd = (kg) ->
        scope.kg = angular.copy kg
        scope.newAd = scope.createNewAd(kg)
        scope.editing = false
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_ad.html'

      scope.editExistingAd = (ad) ->
        scope.newAd = angular.copy ad
        scope.editing = true

      scope.addNewAd = (kg)->
        scope.newAd = scope.createNewAd(school_id: kg)
        scope.editing = false

      scope.delete = (ad) ->
        ad.$delete ->
          scope.kg.ad.splice(scope.kg.ad.indexOf(ad), 1)
          scope.refresh()

      scope.save = (ad) ->
        ad.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.closeModel = ->
        scope.currentModal.hide()

      scope.createNewAd = (kg)->
        new Ad
          school_id: kg.school_id
          position: 1

  ]

angular.module('kulebaoOp').controller 'OpAdsInSchoolCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'classService',
    (scope, rootScope, stateParams, $location, School, Class) ->
      scope.loading = true
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id, (classes)->
          scope.navigateTo(classes[0]) if $location.path().indexOf '/class' <= 0
        scope.loading = false

      scope.navigateTo = (c) ->
        $location.path "main/ad_in_school/#{stateParams.school_id}/class/#{c.class_id}"
  ]

angular.module('kulebaoOp').controller 'OpAdPositionCtrl',
  ['$scope', '$rootScope', '$stateParams', '$location', 'schoolService', 'videoMemberService', 'senderService',
   'relationshipService', 'classService',
    (scope, rootScope, stateParams, $location, School, VideoMember, Parent, Relationship, Class) ->
      scope.loading = true
      scope.current_class = stateParams.class_id
      scope.kindergarten = School.get school_id: stateParams.school_id, ->
        scope.kindergarten.classes = Class.query school_id: stateParams.school_id

      scope.parents = VideoMember.query school_id: stateParams.school_id, (all)->
        _.forEach all, (p) ->
          Parent.get school_id: stateParams.school_id, id: p.id, type: 'p', (data)->
            p.detail = data
            p.reltaionship = Relationship.query school_id: stateParams.school_id, parent: p.detail.phone
        scope.loading = false

      scope.display = (p) ->
        p.reltaionship? && p.reltaionship.$resolved && p.reltaionship[0].child.class_id == parseInt stateParams.class_id

  ]





