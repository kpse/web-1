'use strict'

angular.module('kulebao.services')
.factory 'appPackageService', ['$resource',
  ($resource) ->
    $resource '/app_package', {},
      {
        latest:
          method: 'GET', params: {redirect: false}
      }
]

angular.module('kulebao.services')
.factory 'teacherAppPackageService', ['$resource',
  ($resource) ->
    $resource '/api/v1/teacher_app_package', {}, {latest:
      method: 'GET', params: {redirect: false}
    }


]
