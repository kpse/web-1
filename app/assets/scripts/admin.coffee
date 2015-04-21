angular.module('kulebao.services', ['ngResource'])
angular.module('kulebao.directives', ['kulebao.services'])
angular.module('kulebao.filters', ['kulebao.services'])
angular.module('kulebaoApp', ['ui.router', 'ngResource', 'ngRoute', 'angulartics', 'angulartics.google.analytics', 'kulebao.directives', 'kulebao.filters'])
angular.module('kulebaoAdmin',
  ['kulebaoApp', 'ui.bootstrap', 'ui.mask', 'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap', 'mgcrea.ngStrap.helpers.dimensions', 'mgcrea.ngStrap.tooltip', 'ngDragDrop',
   'ngCookies', 'ui.calendar'])
.config ['$stateProvider', '$urlRouterProvider', '$compileProvider',
  ($stateProvider, $urlRouterProvider, $compileProvider) ->
    $stateProvider
    .state 'kindergarten',
      url: '/kindergarten/:kindergarten',
      templateUrl: 'templates/admin/kindergarten_manage.html',
      controller: 'KgManageCtrl'
      resolve:
        AdminUser : (employeeService) -> employeeService.get().$promise
        School : ($stateParams, schoolService) -> schoolService.get(school_id: $stateParams.kindergarten).$promise
        ClassesInSchool : ($stateParams, classService) -> classService.query(school_id: $stateParams.kindergarten).$promise
    .state 'kindergarten.bulletin',
      url: '/bulletin',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'BulletinManageCtrl'
    .state 'kindergarten.bulletin.class',
      url: '/class/:class',
      templateUrl: 'templates/admin/classes.html',
      controller: 'BulletinManageCtrl'
    .state 'kindergarten.bulletin.class.list',
      url: '/list',
      templateUrl: 'templates/admin/news_in_scope.html',
      controller: 'BulletinCtrl'
    .state 'kindergarten.swipingcard',
      url: '/swipingcard',
      templateUrl: 'templates/admin/swipingcard.html',
      controller: 'AccountCtrl'
    .state 'kindergarten.intro',
      url: '/intro',
      templateUrl: 'templates/admin/intro.html',
      controller: 'IntroCtrl'
    .state 'kindergarten.cookbook',
      url: '/cookbook',
      templateUrl: 'templates/admin/cookbook.html',
      controller: 'CookbookCtrl'
    .state 'kindergarten.schedule',
      url: '/schedule',
      templateUrl: 'templates/admin/schedule.html',
      controller: 'ScheduleCtrl'
    .state 'kindergarten.schedule.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'ScheduleCtrl'
    .state 'kindergarten.schedule.class.list',
      url: '/list',
      templateUrl: 'templates/admin/class_schedule.html',
      controller: 'ClassScheduleCtrl'

    .state 'kindergarten.relationship',
      url: '/relationship',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'RelationshipMainCtrl'
    .state 'kindergarten.relationship.type',
      url: '/type/:type'
      templateUrl: (stateParams) ->
        'templates/admin/' + stateParams.type + '_relationship.html'
      controllerProvider: ($stateParams) ->
        $stateParams.type + "Ctrl"
    .state 'kindergarten.relationship.type.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'ConnectedInClassCtrl'
    .state 'kindergarten.relationship.type.class.list',
      url: '/list',
      templateUrl: 'templates/admin/relationship.html',
      controller: 'ConnectedRelationshipCtrl'
    .state 'kindergarten.relationship.type.preview',
      url: '/preview',
      templateUrl: 'templates/admin/batch_import_preview.html',
      controller: 'batchImportCtrl'
    .state 'kindergarten.relationship.type.preview.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'ImportPreviewRelationshipCtrl'
    .state 'kindergarten.relationship.type.preview.class.list',
      url: '/list',
      templateUrl: 'templates/admin/relationship_preview.html',
      controller: 'ImportPreviewRelationshipCtrl'

    .state 'kindergarten.conversation',
      url: '/conversation',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'ConversationsListCtrl'
    .state 'kindergarten.conversation.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'ConversationsInClassCtrl'
    .state 'kindergarten.conversation.class.list',
      url: '/list',
      templateUrl: 'templates/admin/conversation_in_class.html',
      controller: 'ConversationsInClassCtrl'
    .state 'kindergarten.conversation.class.child',
      url: '/child/:child_id',
      templateUrl: 'templates/admin/conversation.html',
      controller: 'ConversationCtrl'

    .state 'kindergarten.history',
      url: '/history',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'HistoryListCtrl'
    .state 'kindergarten.history.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'HistoryInClassCtrl'
    .state 'kindergarten.history.class.list',
      url: '/list',
      templateUrl: 'templates/admin/history_in_class.html',
      controller: 'HistoryInClassCtrl'
    .state 'kindergarten.history.class.child',
      url: '/child/:child_id',
      templateUrl: 'templates/admin/history.html',
      controller: 'HistoryCtrl'

    .state 'kindergarten.assess',
      url: '/baby-status',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'AssessListCtrl'
    .state 'kindergarten.assess.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'AssessInClassCtrl'
    .state 'kindergarten.assess.class.list',
      url: '/list',
      templateUrl: 'templates/admin/assess_in_class.html',
      controller: 'AssessInClassCtrl'
    .state 'kindergarten.assess.class.child',
      url: '/child/:child',
      templateUrl: 'templates/admin/assess.html',
      controller: 'AssessCtrl'

    .state 'kindergarten.assignment',
      url: '/assignment',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'AssignmentListCtrl'
    .state 'kindergarten.assignment.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'AssignmentsInClassCtrl'
    .state 'kindergarten.assignment.class.list',
      url: '/list',
      templateUrl: 'templates/admin/assignment_in_class.html',
      controller: 'AssignmentsCtrl'

    .state 'kindergarten.member',
      url: '/member',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'MembersListCtrl'
    .state 'kindergarten.member.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'MembersInClassCtrl'
    .state 'kindergarten.member.class.list',
      url: '/list',
      templateUrl: 'templates/admin/member_in_class.html',
      controller: 'MembersInClassCtrl'

    .state 'kindergarten.employee',
      url: '/employee',
      templateUrl: 'templates/admin/employees.html',
      controller: 'EmployeesListCtrl'
    .state 'kindergarten.employee.detail',
      url: '/detail',
      templateUrl: 'templates/admin/employees_detail.html',
      controller: 'EmployeesListCtrl'
    .state 'kindergarten.employee.score',
      url: '/score',
      templateUrl: 'templates/admin/employees_score.html',
      controller: 'EmployeesScoreCtrl'


    .state 'kindergarten.classManagement',
      url: '/class',
      templateUrl: 'templates/admin/classes_management.html',
      controller: 'ClassesManagementCtrl'

    .state 'kindergarten.bus',
      url: '/bus'
      templateUrl: 'templates/admin/search_panel.html'
      controller: 'BusLocationCtrl'
    .state 'kindergarten.bus.plans',
      url: '/plan'
      templateUrl: 'templates/admin/bus_plans.html'
      controller: 'BusLocationCtrl'
    .state 'kindergarten.bus.plans.driver',
      url: '/driver/:driver'
      templateUrl: 'templates/admin/bus_driver_plan.html'
      controller: 'BusPlansCtrl'
    .state 'kindergarten.bus.plans.driver.child',
      url: '/child/:child'
      templateUrl: 'templates/admin/dailylog_of_child.html'
      controller: 'ChildBusPlansCtrl'
    .state 'kindergarten.bus.management',
      url: '/management'
      templateUrl: 'templates/admin/bus_management.html'
      controller: 'BusManagementCtrl'

    .state 'kindergarten.dailylog',
      url: '/dailylog',
      templateUrl: 'templates/admin/search_panel.html',
      controller: 'AllDailyLogCtrl'
    .state 'kindergarten.dailylog.class',
      url: '/class/:class_id',
      templateUrl: 'templates/admin/classes.html',
      controller: 'DailyLogInClassCtrl'
    .state 'kindergarten.dailylog.class.list',
      url: '/list',
      templateUrl: 'templates/admin/dailylog_in_class.html',
      controller: 'DailyLogInClassCtrl'
    .state 'kindergarten.dailylog.class.child',
      url: '/child/:child_id',
      templateUrl: 'templates/admin/dailylog_of_child.html',
      controller: 'ChildDailyLogCtrl'


    .state 'kindergarten.welcome',
      url: '/welcome',
      templateUrl: 'templates/admin/welcome.html',
      controller: 'WelcomeCtrl'

    .state 'expired',
      url: '/expired',
      templateUrl: 'templates/admin/expired.html',
      controller: 'ExpiredCtrl'
    .state 'notFound',
      url: '/404',
      templateUrl: 'templates/admin/404.html',
      controller: 'ExpiredCtrl'
    .state 'unAuthenticated',
      url: '/401',
      templateUrl: 'templates/admin/401.html',
      controller: 'ExpiredCtrl'

    .state 'kindergarten.wip',
      url: '/wip',
      template: '<div>Sorry, we are still in Building...</div><image class="img-responsive" src="assets/images/wip.gif"></image>',
      controller: 'WipCtrl'
    .state 'default',
      url: '/default',
      controller: 'DefaultCtrl'

    $urlRouterProvider.otherwise ($injector, $location) ->
      $location.path '/default' if $location.path().indexOf('#/kindergarten/') < 0

    $compileProvider.debugInfoEnabled(false);
]
.config(($modalProvider) ->
  angular.extend $modalProvider.defaults,
    animation: 'am-fade'
    placement: 'center'
    backdrop: 'static'

).config ($alertProvider) ->
  angular.extend $alertProvider.defaults,
    animation: 'am-fade-and-slide-top'
    placement: 'top'
    type: "danger"
    show: true
    container: '.main-view'
    duration: 3
