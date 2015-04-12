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
          name: '老张'
          phone: '13222228888'
          employee_id: '3_93740362_11222'
        morningPath: 'P-Y-T-H-O-N'
        currentLocation:
          latitude: 123.1111232
          longitude: 321.2131231
          radius: 0.12312
          direction: 0.12312
        plans:
          [
            {name: '小王'},
            {name: '小宋'},
            {name: '小李'},
            {name: '小代'}
          ]
      },{
        id: "2",
        driver:
          name: '老王'
          phone: '13222229999'
          employee_id: '3_93740362_9977'
        morningPath: 'R-U-B-Y'
        currentLocation:
          latitude: 123.1111232
          longitude: 321.2131231
          radius: 0.12312
          direction: 0.12312
        plans:
          [
            {name: '小魏'},
            {name: '小舒'},
            {name: '小吴'},
            {name: '小群'}
          ]
      }]

      scope.navigateTo = (bus) ->
        $state.go('kindergarten.bus.plans.driver', kindergarten: stateParams.kindergarten, driver: bus.driver.employee_id)
  ]

.controller 'BusPlansCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    (scope, rootScope, stateParams) ->
      scope.loading = false
      scope.currentBus = _.find scope.allBuses, (bus) -> bus.driver.employee_id == stateParams.driver
      scope.addChildPlan = (driver) ->

      scope.navigateTo(scope.allBuses[0]) unless scope.currentBus?
  ]

.controller 'BusManagementCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    (scope, rootScope, stateParams) ->
      scope.mapOf = (bus) ->
        console.log bus.currentLocation
      scope.addBus = ->

      scope.deleteBus = (bus) ->

  ]