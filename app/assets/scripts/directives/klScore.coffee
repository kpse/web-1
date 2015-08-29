'use strict'

angular.module("kulebao.directives").directive "klScoreBoard",
  ['$q', 'StatsService',
    ($q, Stats) ->
      return (
        restrict: "A"
        require: 'ngModel'
        scope:
          ngModel: "="

        link: (scope, element, attrs, c) ->
          scope.employee = scope.ngModel
          all = $q.all [Stats('assess').query(school_id: scope.employee.school_id, employee_id: scope.employee.id).$promise,
                        Stats('conversation').query(school_id: scope.employee.school_id, employee_id: scope.employee.id).$promise,
                        Stats('news').query(school_id: scope.employee.school_id, employee_id: scope.employee.id).$promise]
          all.then (q) ->
            scope.employee.assess = q[0]
            scope.employee.conversation = q[1]
            scope.employee.news = q[2]

        templateUrl: '/templates/directives/kl_score_board.html'
      )

  ]