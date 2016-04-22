angular.module('kulebaoOp').controller 'OpAppCtrl',
  ['$scope', '$rootScope', '$stateParams', 'appPackageService',
   '$location', 'teacherAppPackageService', 'pcPackageService',
    (scope, $rootScope, $stateParams, AppPackage, location, TeacherAppPackage, PCPackage) ->
      $rootScope.tabName = 'app'

      isTeacherPackage = ->
        $stateParams.type == 'teacher'

      scope.isPCPackage = ->
        $stateParams.type == 'pc'

      scope.createPkg = ->
        app = new AppPackage
        app.package_type = 'teacher' if isTeacherPackage()
        app.package_type = 'pc' if scope.isPCPackage()
        app

      scope.PackageService = switch $stateParams.type
        when "teacher" then TeacherAppPackage
        when "pc" then PCPackage
        else AppPackage

      scope.sources = [
        {name: '安卓家长', type: 'parent'}
        {name: '安卓教师', type: 'teacher'}
        {name: 'PC端', type: 'pc'}
      ]


      scope.navigateTo = (source) ->
        location.path "/main/app/type/#{source.type}/detail" if $stateParams.source != source.type

      if $stateParams.type is undefined
        scope.navigateTo scope.sources[0]

      scope.current_source = $stateParams.type

  ]

.controller 'OpAppDetailCtrl',
  ['$scope', '$rootScope', '$stateParams', '$state', 'appUploadService',
    (scope, $rootScope, $stateParams, $state, remoteFileSetter) ->
      scope.lastApp = scope.PackageService.latest ->
        scope.app = scope.createPkg()
        scope.app.version_code = scope.lastApp.version_code + 1

      scope.doUpload = (pic) ->
        scope.uploading = true
        remoteFileSetter pic, scope.adminUser.id, (url) ->
          scope.$apply ->
            scope.app.url = url
            console.log scope.app
            scope.app.$save ->
              scope.lastApp = scope.PackageService.latest ->
                scope.app = scope.createPkg()
                scope.app.version_code = scope.lastApp.version_code + 1
                scope.uploading = false

      scope.savePackage = ->
        scope.app.file_size = 0
        scope.app.$save ->
          scope.lastApp = scope.PackageService.latest ->
            scope.app = scope.createPkg()
            scope.app.version_code = scope.lastApp.version_code + 1
            scope.uploading = false
        

      scope.cleanFields = ->
        scope.app = createPkg()

      scope.delete = (pkg) ->
        pkg.$delete ->
          $state.reload()
  ]



