'use strict'

angular.module('kulebaoAdmin')
.controller 'BusLocationCtrl',
  [ '$scope', '$rootScope', '$stateParams', '$location', 'dailyLogService',
    'childService', 'accessClassService', 'imageCompressService',
    (scope, rootScope, stateParams, location, DailyLog, Child, AccessClass, Compressor) ->
      rootScope.tabName = 'bus-location'
      scope.heading = '班车信息查询'

  ]