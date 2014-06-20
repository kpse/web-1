'use strict'

appPackageService = ($resource) ->
  $resource '/app_package', {}, {latest:
    method: 'GET', params: {redirect: false}}


angular.module('kulebaoAdmin')
.factory('appPackageService', ['$resource', appPackageService])


teacherAppPackageService = ($resource) ->
  $resource '/api/v1/teacher_app_package', {}, {latest:
    method: 'GET', params: {redirect: false}}


angular.module('kulebaoAdmin')
.factory('teacherAppPackageService', ['$resource', teacherAppPackageService])
