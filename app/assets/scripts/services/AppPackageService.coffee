'use strict'

angular.module('kulebao.services')
.factory 'appPackageService', ['$resource',
  ($resource) ->
    $resource '/app_package/:id', {
        id: '@id'
      }, {
        latest:
          method: 'GET', params: redirect: false
      }
]

angular.module('kulebao.services')
.factory 'teacherAppPackageService', ['$resource',
  ($resource) ->
    $resource '/api/v1/teacher_app_package/:id', {
      id: '@id'
    }, {
      latest:
        method: 'GET', params: redirect: false
    }
]

angular.module('kulebao.services')
.factory 'pcPackageService', ['$resource',
  ($resource) ->
    $resource '/api/v1/pc_package/:id', {
      id: '@id'
    }, {
      latest:
        method: 'GET', params: redirect: false
    }
]
