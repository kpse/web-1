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
        scope.navigateTo(scope.allBuses[0]) if scope.allBuses.length > 0 && !$state.includes('kindergarten.bus.plans.driver')

      scope.navigateTo = (bus) ->
        $state.go('kindergarten.bus.plans.driver', kindergarten: stateParams.kindergarten, driver: bus.driver.id)

  ]

.controller 'BusPlansCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$q', '$state', '$animate', '$modal', '$dropdown',
    'busDriverService', 'childService', 'schoolBusService', 'childrenPlanService', 'classService',
    (scope, rootScope, stateParams, $q, $state, $animate, $modal, $dropdown, BusDriver, Child, Bus, Plan, Classes) ->
      scope.loading = false

      findChild = (id) ->
        _.find scope.allChildren, (c) -> c.child_id == id

      scope.allChildren = []
      scope.currentBus =
        plans: []
      scope.refresh = ->
        scope.loading = true
        busQ = Bus.query(school_id: stateParams.kindergarten).$promise
        childQ = Child.query(school_id: stateParams.kindergarten).$promise
        classQ = Classes.query(school_id: stateParams.kindergarten).$promise
        $q.all([busQ, childQ, classQ]).then (q) ->
          scope.allBuses = q[0]
          scope.allChildren = q[1]
          scope.allClasses = q[2]
          scope.currentBus = _.find scope.allBuses, (bus) -> bus.driver.id == stateParams.driver
          BusDriver.query school_id: stateParams.kindergarten, driver: scope.currentBus.driver.id, (data) ->
            scope.currentBus.plans = _.map data, (plan) -> findChild(plan.child_id)
            scope.waitingChildren = scope.childrenWithoutPlan()
            _.forEach scope.waitingChildren, (c) -> Plan.get c, (-> c.hasPlan = true), (-> c.hasPlan = false)
          scope.loading = false

      scope.inClass = (clazz, children) ->
        _.filter children, (c) -> c.class_id == clazz.class_id

      scope.hasChild = (clazz, children) ->
        _.some children, (c) -> c.class_id == clazz.class_id

      scope.childrenWithoutPlan = ->
        _.reject scope.allChildren, (c) ->
          _.any scope.currentBus.plans, (p) -> p.child_id == c.child_id

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
            "divider": true
          },{
            "text": '删除',
            "click": "delete(child)"
          },{
          "divider": true
          }
        ].concat scope.busesDropDown(child)

      scope.busesDropDown = (child) ->
        _.map (_.reject scope.allBuses, (b1) -> b1.id == scope.currentBus.id), (b) ->
          {
            text: "切换到班车:#{b.name}"
            click: "transferTo(child, #{b.id})"
          }

      scope.cancelEditingPlan = ->
        scope.addingPlan = false
        scope.refresh()
        scope.backupPlans = []

      scope.saveChildPlan = (driver) ->
        scope.loading = false
        scope.addingPlan = false
        queue = _.map scope.currentBus.plans, (p) ->
          plan = new Plan
            driver_id: driver.id
            school_id: driver.school_id
            child_id: p.child_id
          plan.$save().$promise
        removedQueue = _(scope.backupPlans).chain().reject((ea) -> _.any scope.currentBus.plans, (eb) -> ea.child_id == eb.child_id)
        .map((p) -> Plan.delete(driver_id: driver.id, school_id: driver.school_id, child_id: p.child_id).$promise).compact().value()

        $q.all(queue.concat(removedQueue)).then ->
          scope.refresh()

      scope.addChildPlan = ->
        scope.addingPlan = true
        scope.backupPlans = angular.copy scope.currentBus.plans

      scope.delete = (child) ->
        #TODO: why animation exception here? need more investigate
        $animate.enabled false
        Plan.delete driver_id: scope.currentBus.driver.id, school_id: scope.currentBus.driver.school_id, child_id: child.child_id, ->
          scope.currentBus.plans = _.reject scope.currentBus.plans, (p) -> p.child_id == child.child_id
          $animate.enabled true


      scope.show = (child) ->
        scope.child = angular.copy child
        scope.onlySave = true
        scope.currentModal = $modal
          scope: scope
          contentTemplate: 'templates/admin/add_child.html'

      scope.saveChild = (child) ->
        Child.save child, ->
          scope.currentModal.hide()
          scope.refresh()

      scope.transferTo = (child, busId) ->
        bus = _.find scope.allBuses, (b) -> b.id == busId
        $animate.enabled false
        Plan.save driver_id: bus.driver.id, school_id: bus.driver.school_id, child_id: child.child_id, ->
          $animate.enabled true
          scope.refresh()

      scope.showBusLog = (child) ->
        $state.go('kindergarten.bus.plans.driver.child', kindergarten: child.school_id, driver: scope.currentBus.driver.id, child: child.child_id)

      scope.beforeDropInBus = (event, dropped) ->
        $q (resolve, reject) ->
          if (_.any scope.currentBus.plans, (r) -> r.name == dropped.draggable[0].innerText)
            reject()
          else
            resolve()

      scope.beforeDropInWaiting = (event, dropped) ->
        $q (resolve, reject) ->
          if (_.any scope.waitingChildren, (r) -> r.name == dropped.draggable[0].innerText)
            reject()
          else
            resolve()

      scope.dropInBus = (event, dropped) ->
        scope.waitingChildren = _.reject scope.waitingChildren, (r) -> r.name == dropped.draggable[0].innerText

      scope.dropInWaiting = (event, dropped) ->
        scope.currentBus.plans = _.reject scope.currentBus.plans, (r) -> r.name == dropped.draggable[0].innerText
  ]

.controller 'BusManagementCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$modal', '$state', 'schoolBusService', 'schoolEmployeesService', 'busLocationService',
    (scope, rootScope, stateParams, Modal, $state, Bus, Driver, Location) ->
      scope.mapOf = (bus) ->
        $state.go 'kindergarten.bus.management.map', kindergarten: stateParams.kindergarten, driver: bus.driver.id

      scope.movingTest = (bus) ->
        Location.save data = {"school_id":bus.driver.school_id,"driver_id":bus.driver.id,"latitude":39.915,"longitude":116.404,"direction":180.89999389648438,"radius":0.8999999761581421,"address":"天安门"}, ->
          alert "#{bus.name}已到达天安门。"

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

      scope.deleteBus = (bus) ->
        bus.$delete ->
          scope.refresh()
      scope.saveBus = (bus) ->
        bus.$save ->
          scope.currentModal.hide()
          scope.refresh()

      isADriver = (employee) -> _.any scope.allBuses, (f) -> f.driver.id == employee.id
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

.controller 'ChildBusPlansCtrl',
  [ '$scope', '$rootScope', '$stateParams',
    '$state', '$q', 'childService', 'singleDailyLogService', 'relationshipService',
    'classManagerService',
    (scope, rootScope, stateParams, $state, $q, Child, DailyLog, Relationship, Manager) ->

      retrieveChild = ->
        if scope.allChildren?
          $q (resolve, reject) ->
            child = _.find scope.allChildren, (c) -> c.child_id == stateParams.child
            resolve(child)
        else
          Child.get(school_id: stateParams.kindergarten, child_id: stateParams.child).$promise
      retrieveChild().then (data)->
        scope.child = data
        scope.heading = "#{scope.child.name}的乘车记录"
        scope.relationships = Relationship.query school_id: stateParams.kindergarten, child: scope.child.child_id
        scope.managers = Manager.query school_id: stateParams.kindergarten, class_id: scope.child.class_id

        DailyLog.query school_id: stateParams.kindergarten, child_id: stateParams.child, (data) ->
          scope.allLogs = _.filter data, (l) -> l.notice_type > 3

      scope.displayType = (type) ->
        switch type
          when 0 then '到校'
          when 1 then '离校'
          when 10 then '早上上车'
          when 11 then '早上下车'
          when 12 then '下午上车'
          when 13 then '下午下车'

  ]

.controller 'BusOnTheMapCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$state', 'schoolBusService', 'schoolEmployeesService', 'busLocationService',
    (scope, rootScope, stateParams, $state, Bus, Driver, Location) ->
      scope.loading = true
      Location.query school_id: stateParams.kindergarten, driver_id: stateParams.driver, (data)->
        console.log data[0]
        currentLocation = data[0]
        scope.mapOptions = currentLocation
        scope.loading = false
  ]