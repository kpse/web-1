angular.module('kulebaoOp').controller 'OpTeacherScoreCtrl',
  ['$scope', '$rootScope', 'allEmployeesService', 'StatsService', 'schoolService', '$q',
    (scope, rootScope, Employee, Stats, School, $q) ->
      rootScope.tabName = 'score'
      rootScope.loading = true
      all = $q.all [Stats('assess').query().$promise,
                    Stats('conversation').query().$promise,
                    Stats('news').query().$promise,
                    Employee.query().$promise,
                    School.query().$promise]
      all.then (q) ->
        scope.assess = q[0]
        scope.conversation = q[1]
        scope.news = q[2]
        scope.allEmployees = q[3]
        scope.schools = q[4]
        _.each scope.allEmployees, (e) ->
          e.assess = _.find scope.assess, (a) -> a.employee_id == e.id
          e.conversation = _.find scope.conversation, (a) -> a.employee_id == e.id
          e.news = _.find scope.news, (a) -> a.employee_id == e.id
          e.school = _.find scope.schools, (a) -> a.school_id == e.school_id
          e.count = scope.countScore(e)
        rootScope.loading = false

      scope.noOperator = (employee) -> employee.privilege_group != 'operator'

      safeCount = (e) ->
        try
          e.count
        catch error
          0

      scope.countScore = (employee) ->
        safeCount(employee.news) + safeCount(employee.conversation) + safeCount(employee.assess)

      scope.nonZero = (employee) -> employee.count > 0
  ]





