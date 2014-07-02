angular.module('kulebaoOp').controller 'OpTeacherScoreCtrl',
  ['$scope', '$rootScope', 'employeeService', 'allEmployeesService', 'StatsService', 'schoolService', '$q',
    (scope, rootScope, CurrentUser, Employee, Stats, School, $q) ->
      rootScope.tabName = 'score'
      scope.adminUser = CurrentUser.get()

      all = $q.all [Stats('assignment').query().$promise,
                    Stats('assess').query().$promise,
                    Stats('conversation').query().$promise,
                    Stats('news').query().$promise,
                    Employee.query().$promise,
                    School.query().$promise]
      all.then (q) ->
        scope.assignment = q[0]
        scope.assess = q[1]
        scope.conversation = q[2]
        scope.news = q[3]
        scope.employees = q[4]
        scope.schools = q[5]
        _.forEach scope.employees, (e) ->
          e.assignment = _.find scope.assignment, (a) -> a.employee_id == e.id
          e.assess = _.find scope.assess, (a) -> a.employee_id == e.id
          e.conversation = _.find scope.conversation, (a) -> a.employee_id == e.id
          e.news = _.find scope.news, (a) -> a.employee_id == e.id
          e.school = _.find scope.schools, (a) -> a.school_id == e.school_id
          e.count = scope.countScore(e)

      scope.noOperator = (employee) -> employee.privilege_group != 'operator'

      safeCount = (e) ->
        try
          e.count
        catch error
          0

      scope.countScore = (employee) ->
        safeCount(employee.assignment) + safeCount(employee.news) + safeCount(employee.conversation) + safeCount(employee.assess)

      scope.nonZero = (employee) -> scope.countScore(employee) > 0
  ]





