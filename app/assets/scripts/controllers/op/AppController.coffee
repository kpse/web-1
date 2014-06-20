angular.module('kulebaoOp').controller 'OpAppCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http', 'appUploadService', 'appPackageService', 'employeeService',
   '$location', 'teacherAppPackageService',
    (scope, $rootScope, $stateParams, $http, remoteFileSetter, AppPackage, Employee, location, TeacherAppPackage) ->
      $rootScope.tabName = 'app'

      scope.adminUser = Employee.get()

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
        location.path(location.path().replace(/\/type\/.+$/,
          '') + '/type/' + source.type + '/detail') if $stateParams.type != source.type

      if $stateParams.type is undefined
        scope.navigateTo scope.sources[0]

      scope.current_source = $stateParams.type

  ]

angular.module('kulebaoOp').controller 'OpAppDetailCtrl',
  ['$scope', '$rootScope', '$stateParams', '$http', 'appUploadService', 'appPackageService', 'employeeService',
   '$location', 'teacherAppPackageService',
    (scope, $rootScope, $stateParams, $http, remoteFileSetter, AppPackage, Employee, location, TeacherAppPackage) ->

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



