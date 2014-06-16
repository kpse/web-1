angular.module('kulebaoOp').controller 'OpDeviceCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', 'allEmployeesService', '$resource', '$alert',
    (scope, rootScope, School, Clazz, Employee, $resource, Alert) ->
      rootScope.tabName = 'device'
      Device = $resource '/api/v1/device'

      scope.refresh = ->
        scope.allSchools = School.query ->
          scope.allDevices = Device.query ->
            _.forEach scope.allDevices, (d) ->
              d.school_name = (_.find scope.allSchools, (s) -> s.school_id == d.school_id).name
        scope.device = new Device

      scope.loading = true
      scope.employees = Employee.query ->
        scope.refresh()
        scope.loading = false


      scope.save = (device) ->
        device.$save ->
          scope.refresh()
          scope.form.$setPristine();
          scope.editing = false
        , (res) ->
          Alert
            title: '添加设备失败'
            content: res.data.error_msg

      scope.delete = ->
      scope.edit = ->

  ]

