class Config
  constructor: ($stateProvider, $urlRouterProvider) ->
    $stateProvider
    .state 'kindergarten',
      url: '/kindergarten/:kindergarten',
      templateUrl: 'templates/admin/kindergarten_manage.html',
      controller: 'KgManageCtrl'
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
      templateUrl: 'templates/admin/class_schedule.html',
      controller: 'ClassScheduleCtrl'
    .state 'kindergarten.relationship',
      url: '/relationship',
      templateUrl: 'templates/admin/relationship.html',
      controller: 'RelationshipCtrl'
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
    .state 'kindergarten.conversation.class.relationship',
      url: '/card/:card',
      templateUrl: 'templates/admin/conversation.html',
      controller: 'ConversationCtrl'

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
    .state 'kindergarten.classManagement',
      url: '/class',
      templateUrl: 'templates/admin/classes_management.html',
      controller: 'ClassesManagementCtrl'
#deprecated
    .state 'kindergarten.parents',
      url: '/parents',
      templateUrl: 'templates/admin/parents.html',
      controller: 'ParentsCtrl'
    .state 'kindergarten.parents.class',
      url: '/class/:classId',
      templateUrl: 'templates/admin/classes.html',
      controller: 'ParentsInClassCtrl'
    .state 'kindergarten.parents.class.list',
      url: '/list',
      templateUrl: 'templates/admin/list_parents.html',
      controller: 'ParentsInClassCtrl'
    .state 'kindergarten.parents.class.connect_child',
      url: '/connect_child',
      templateUrl: 'templates/admin/choose_existing_child.html',
      controller: 'ConnectToChildCtrl'

    .state 'kindergarten.welcome',
      url: '/welcome',
      templateUrl: 'templates/admin/welcome.html',
      controller: 'WelcomeCtrl'

    .state 'expired',
      url: '/expired',
      templateUrl: 'templates/admin/expired.html',
      controller: 'ExpiredCtrl'

    .state 'kindergarten.wip',
      url: '/wip',
      template: '<div>Sorry, we are still in Building...</div><image class="img-responsive" src="assets/images/wip.gif"></image>',
      controller: 'WipCtrl'
    .state 'default',
      url: '/default',
      controller: 'DefaultCtrl'

    $urlRouterProvider.otherwise ($injector, $location) ->
      $location.path '/default'


angular.module('kulebaoApp', ['ui.router', 'ngResource', 'ngRoute', 'angulartics', 'angulartics.google.analytics'])
angular.module('kulebaoAdmin',
  ['kulebaoApp', 'ui.router', 'ngResource', 'ngRoute', 'ui.bootstrap', 'ui.mask', 'angulartics',
   'angulartics.google.analytics', 'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap', 'mgcrea.ngStrap.helpers.dimensions',
   'ngCookies'])
.config ['$stateProvider', '$urlRouterProvider', Config]
