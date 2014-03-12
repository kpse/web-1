angular.module('kulebaoOp').controller 'OpSchoolCtrl',
  ['$scope', '$rootScope', 'schoolService', 'classService', '$modal'
    (scope, rootScope, School, Clazz, Modal) ->

      scope.refresh = ->
        scope.kindergartens = School.query ->
          _.each scope.kindergartens, (kg) ->
            kg.manager =
              name: '空条承太郎'

      scope.refresh()

      rootScope.tabName = 'school'

      scope.edit = (clazz) ->
        alert('编辑 "' + clazz.name + '"!')

      scope.delete = ->
        alert('暂未实现')

      scope.newSchool = ->
        new School
          school_id: nextId(scope.kindergartens)

      scope.addSchool = ->
        scope.school = scope.newSchool()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_school.html'

      scope.save = (school) ->
        school.$save ->
          scope.refresh()
          scope.currentModal.hide()

      scope.isDuplicated = (school) ->
        return false if school.school_id is undefined
        undefined isnt _.find scope.kindergartens, (k) ->
          k.school_id == school.school_id

      nextId = (schools)->
        1 + _.max _.map schools, (c) -> c.school_id



  ]

