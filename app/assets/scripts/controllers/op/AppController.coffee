angular.module('kulebaoOp').controller 'OpAppCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http', 'appUploadService', 'appPackageService',
   '$location', 'teacherAppPackageService',
    (scope, $rootScope, $stateParams, $http, remoteFileSetter, AppPackage, location, TeacherAppPackage) ->
      $rootScope.tabName = 'app'

      isTeacherPackage = ->
        $stateParams.type == 'teacher'

      scope.createPkg = ->
        app = new AppPackage
        app.package_type = 'teacher' if isTeacherPackage()
        app

      scope.PackageService = if isTeacherPackage() then TeacherAppPackage else AppPackage

      scope.sources = [
        {name: '安卓家长', type: 'parent'}
        {name: '安卓教师', type: 'teacher'}
      ]


      scope.navigateTo = (source) ->
        location.path "/main/app/type/#{source.type}/detail" if $stateParams.source != source.type

      if $stateParams.type is undefined
        scope.navigateTo scope.sources[0]

      scope.current_source = $stateParams.type

  ]

.controller 'OpAppDetailCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http', 'appUploadService',
    (scope, $rootScope, $stateParams, $http, remoteFileSetter) ->
      scope.lastApp = scope.PackageService.latest ->
        scope.app = scope.createPkg()
        scope.app.version_code = scope.lastApp.version_code + 1

      scope.doUpload = (pic) ->
        scope.uploading = true
        remoteFileSetter pic, (url) ->
          scope.$apply ->
            scope.app.url = url
            console.log scope.app
            scope.app.$save ->
              scope.lastApp = scope.PackageService.latest ->
                scope.app = scope.createPkg()
                scope.app.version_code = scope.lastApp.version_code + 1
                scope.uploading = false
        , scope.adminUser.id

      scope.cleanFields = ->
        scope.app = createPkg()

  ]



