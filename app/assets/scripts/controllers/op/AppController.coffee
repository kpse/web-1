angular.module('kulebaoOp').controller 'OpAppCtrl',
  ['$scope', '$rootScope',
   '$stateParams', '$http',
   'uploadService', 'appPackageService', '$timeout',

    ($scope, $rootScope, $stateParams, $http, uploadService, appPackageService, timeout) ->
      $scope.lastApp = appPackageService.latest(->
        $scope.app.version_code = $scope.lastApp.version_code + 1)
      $scope.app = new appPackageService

      $rootScope.tabName = 'app'

      $scope.doUpload = (pic) ->
        $scope.uploading = true
        uploadService pic, (url) ->
          $scope.$apply ->
            $scope.app.url = url
            console.log $scope.app
            $scope.app.$save ->
              $scope.lastApp = appPackageService.latest ->
                $scope.app = new appPackageService
                  version_code: $scope.lastApp.version_code + 1
                $scope.uploading = false

      $scope.cleanFields = ->
        $scope.app = new appPackageService

  ]




