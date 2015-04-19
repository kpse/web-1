'use strict'

angular.module('kulebaoAdmin')
.controller 'BusLocationCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$state',
    (scope, rootScope, stateParams, $state) ->
      rootScope.tabName = 'bus-location'
      scope.heading = '班车信息查询'

      scope.navigateToCreating = ->
        $state.go('kindergarten.bus.management', kindergarten: stateParams.kindergarten)
      scope.navigateToPlan = ->
        $state.go('kindergarten.bus.plans', kindergarten: stateParams.kindergarten)

      scope.reverse = (path) ->
        path.split('-').reverse().join('-')

      scope.allBuses = [{
        id: "1",
        driver:
          name: '富贵'
          phone: '13222228888'
          employee_id: '3_93740362_9977'
        morningPath: 'P-Y-T-H-O-N'
        currentLocation:
          latitude: 123.1111232
          longitude: 321.2131231
          radius: 0.12312
          direction: 0.12312
        plans:[]
      },{
        id: "2",
        driver:
          name: '老王'
          phone: '13222229999'
          employee_id: '3_93740362_1022'
        morningPath: 'R-U-B-Y'
        currentLocation:
          latitude: 123.1111232
          longitude: 321.2131231
          radius: 0.12312
          direction: 0.12312
        plans:[]
      }]

      scope.navigateTo = (bus) ->
        $state.go('kindergarten.bus.plans.driver', kindergarten: stateParams.kindergarten, driver: bus.driver.employee_id)

      scope.navigateTo(scope.allBuses[0]) if scope.allBuses.length > 0
  ]

.controller 'BusPlansCtrl',
  [ '$scope', '$rootScope', '$stateParams', 'busDriverService', 'childService',
    (scope, rootScope, stateParams, BusDriver, Child) ->
      scope.loading = false
      scope.currentBus = _.find scope.allBuses, (bus) -> bus.driver.employee_id == stateParams.driver

      scope.refresh = ->
        return unless scope.currentBus?
        BusDriver.query school_id: stateParams.kindergarten, driver: scope.currentBus.driver.employee_id, (data) ->
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
  [ '$scope', '$rootScope', '$stateParams',
    (scope, rootScope, stateParams) ->
      scope.mapOf = (bus) ->
        console.log bus.currentLocation
      scope.addBus = -> alert("添加班车")

      scope.deleteBus = (bus) -> alert("删除班车#{bus.id}")

  ]