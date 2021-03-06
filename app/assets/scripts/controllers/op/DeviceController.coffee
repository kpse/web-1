angular.module('kulebaoOp').controller 'OpDeviceCtrl',
  ['$scope', '$rootScope', 'schoolService', '$resource', '$alert',
    (scope, rootScope, School, $resource, Alert) ->
      rootScope.tabName = 'device'

      Device = $resource '/api/v1/device/:id', {
        id: '@id'
      }

      scope.refresh = ->
        rootScope.loading = true
        scope.allSchools = School.query ->
          scope.allDevices = Device.query ->
            _.each scope.allDevices, (d) ->
              school = _.find scope.allSchools, (s) -> s.school_id == d.school_id
              d.school_name = if school? then school.name else '该学校数据已删除'
        scope.device = new Device
        rootScope.loading = false

      scope.refresh()

      scope.save = (device) ->
        device.$save ->
          scope.refresh()
          scope.form.$setPristine();
        , (res) ->
          handleError('设备地址', res)

      scope.delete = (device)->
        device.$delete ->
          scope.refresh()
          scope.form.$setPristine();

      scope.deviceEditing = -1
      scope.editDevice = (device) ->
        scope.deviceEditing = device.id
        scope.oldDevice = angular.copy device

      scope.cancelEditing = (device)->
        scope.deviceEditing = -1
        device.mac = scope.oldDevice.mac

      handleError = (obj, res) ->
        Alert
          title: "保存#{obj}失败"
          content: res.data.error_msg
          placement: "top-left"
          type: "danger"
          container: '.panel-body'
          duration: 3

      scope.updateDevice = (device) ->
        device.$save ->
          scope.refresh()
          scope.deviceEditing = -1
        , (res) ->
          handleError('设备地址', res)

  ]

