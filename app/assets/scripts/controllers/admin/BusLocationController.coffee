'use strict'

angular.module('kulebaoAdmin')
.controller 'BusLocationCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$state', 'schoolBusService',
    (scope, rootScope, stateParams, $state, Bus) ->
      rootScope.tabName = 'bus-location'
      scope.heading = '班车信息查询'

      scope.navigateToCreating = ->
        $state.go('kindergarten.bus.management', kindergarten: stateParams.kindergarten)
      scope.navigateToPlan = ->
        $state.go('kindergarten.bus.plans', kindergarten: stateParams.kindergarten)

      scope.reverse = (path) ->
        path.split('-').reverse().join('-')

      scope.allBuses = Bus.query school_id: stateParams.kindergarten, ->
        scope.navigateTo(scope.allBuses[0]) if scope.allBuses.length > 0

      scope.navigateTo = (bus) ->
        $state.go('kindergarten.bus.plans.driver', kindergarten: stateParams.kindergarten, driver: bus.driver.id)

  ]

.controller 'BusPlansCtrl',
  [ '$scope', '$rootScope', '$stateParams', 'busDriverService', 'childService', 'schoolBusService',
    (scope, rootScope, stateParams, BusDriver, Child, Bus) ->
      scope.loading = false

      scope.refresh = ->
        scope.allBuses = Bus.query school_id: stateParams.kindergarten, ->
          scope.currentBus = _.find scope.allBuses, (bus) -> bus.driver.id == stateParams.driver
          BusDriver.query school_id: stateParams.kindergarten, driver: scope.currentBus.driver.id, (data) ->
            scope.currentBus.plans = _.map data, (plan) -> Child.get school_id: plan.school_id, child_id: plan.child_id

      scope.refresh()

      scope.addChildPlan = (driver) -> alert("添加一个小孩给#{driver.name}")

      scope.childDropDown = (child) ->
        [
          {
            "text": '查看信息',
            "click": "show(child)"
          },{
          "divider": true
          },
          {
            "text": '删除',
            "click": "delete(child)"
          }
        ]

      scope.delete = (child) -> alert("删除小孩#{child.name}")
      scope.show = (child) -> alert("详细信息#{child.name}")

  ]

.controller 'BusManagementCtrl',
  [ '$scope', '$rootScope', '$stateParams', 'schoolBusService',
    (scope, rootScope, stateParams, Bus) ->
      scope.mapOf = (bus) ->
        console.log bus.currentLocation

      scope.addBus = -> alert("添加班车")

      scope.deleteBus = (bus) -> alert("删除班车#{bus.id}")

      scope.refresh = ->
        scope.allBuses = Bus.query school_id: stateParams.kindergarten

      scope.refresh()
  ]