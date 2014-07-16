angular.module('kulebaoOp').controller 'OpPrincipalCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', 'allEmployeesService', '$modal', 'uploadService', '$q',
   'StatsService',
    (scope, rootScope, School, Clazz, Employee, Modal, Upload, $q, Stats) ->
      rootScope.tabName = 'principal'

      scope.refresh = ->
        scope.loading = true
        scope.employees = Employee.query ->
          scope.loading = false

      scope.refresh()

      scope.edit = (employee) ->
        scope.employee = angular.copy employee
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/admin/add_employee.html'

      scope.delete = (employee) ->
        employee.$delete ->
          scope.refresh()

      scope.save = (object) ->
        object.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.uploadPic = (employee, pic) ->
        scope.uploading = true
        Upload pic, scope.adminUser.id, (url) ->
          scope.$apply ->
            employee.portrait = url if url isnt undefined
            scope.uploading = false

  ]

