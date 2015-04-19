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
      scope.childDropDown = (child) ->
        [
          {
            "text": '查看信息',
            "click": "show(child)"
          },{
            "text": '查看乘车记录',
            "click": "showBusLog(child)"
          },{
            "text": '切换班车',
            "click": "switchBus(child)"
          },{
          "divider": true
          },{
            "text": '删除',
            "click": "delete(child)"
          }
        ]

      scope.addChildPlan = (driver) -> alert("添加一个小孩给驾驶员#{driver.name}")
      scope.delete = (child) -> alert("删除小孩#{child.name}")
      scope.show = (child) -> alert("详细信息#{child.name}")
      scope.switchBus = (child) -> alert("切换到其他班车#{child.name}")
      scope.showBusLog = (child) -> alert("查看#{child.name}的乘车历史记录")

  ]

.controller 'BusManagementCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$modal', 'schoolBusService', 'schoolEmployeesService'
    (scope, rootScope, stateParams, Modal, Bus, Driver) ->
      scope.mapOf = (bus) ->
        console.log bus.currentLocation

      createBus = ->
        new Bus
          school_id: parseInt stateParams.kindergarten
          morning_path: '长春-哈尔滨-北京'
          evening_path: '美国-加州-墨西哥'
          morning_start: '8:30'
          morning_end: '9:30'
          evening_start: '15:30'
          evening_end: '16:30'

      scope.addBus = ->
        scope.newBus = createBus()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_bus.html'

      scope.deleteBus = (bus) -> alert("删除班车#{bus.id}")
      scope.saveBus = (bus) ->
        bus.$save ->
          scope.currentModal.hide()
          scope.refresh()

      isADriver = (employee) -> undefined isnt _.find scope.allBuses, (f) -> f.driver.id == employee.id
      availableDriver = (employees) -> _.filter employees, (d) -> !isADriver(d)

      scope.refresh = ->
        scope.loading = true
        scope.allBuses = Bus.query school_id: stateParams.kindergarten, ->
          scope.loading = false
        Driver.query school_id: stateParams.kindergarten, (employees) ->
          scope.drivers = _.map availableDriver(employees), (e) ->
            e.value = e.name
            e

      scope.refresh()
  ]