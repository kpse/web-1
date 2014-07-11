angular.module('kulebao.services', ['ngResource'])
angular.module('kulebao.directives', ['kulebao.services'])
angular.module('kulebaoApp', ['ui.router', 'ngResource', 'ngRoute', 'angulartics', 'angulartics.google.analytics', 'kulebao.directives'])
angular.module('kulebaoAdmin',
  ['kulebaoApp', 'ui.router', 'ngResource', 'ngRoute', 'ui.bootstrap', 'ui.mask', 'angulartics',
   'angulartics.google.analytics', 'ngCookies'])
angular.module('kulebaoOp',
  ['kulebaoAdmin', 'kulebaoApp', 'ui.router', 'ngResource', 'ngRoute', 'ui.bootstrap', 'ui.mask', 'angulartics',
   'angulartics.google.analytics', 'ngCookies', 'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap', 'emoji'])
.config ['$stateProvider', '$urlRouterProvider',
  ($stateProvider, $urlRouterProvider) ->
    $stateProvider
    .state 'main',
      url: '/main',
      templateUrl: 'templates/op/main.html',
      controller: 'OpCtrl'
    .state 'main.app',
      url: '/app',
      templateUrl: 'templates/op/app_release.html',
      controller: 'OpAppCtrl'
    .state 'main.app.type',
      url: '/type/:type',
      templateUrl: 'templates/op/all_sources.html',
      controller: 'OpAppCtrl'
    .state 'main.app.type.detail',
      url: '/detail',
      templateUrl: 'templates/op/app.html',
      controller: 'OpAppDetailCtrl'

    .state 'main.school',
      url: '/school',
      templateUrl: 'templates/op/school.html',
      controller: 'OpSchoolCtrl'

    .state 'main.feedback',
      url: '/feedback',
      templateUrl: 'templates/op/feedback.html',
      controller: 'OpFeedbackCtrl'
    .state 'main.feedback.source',
      url: '/source/:source',
      templateUrl: 'templates/op/all_sources.html',
      controller: 'OpFeedbackCtrl'
    .state 'main.feedback.source.list',
      url: '/list',
      templateUrl: 'templates/op/feedback_source.html',
      controller: 'OpFeedbackSourceCtrl'

    .state 'main.principal',
      url: '/principal',
      templateUrl: 'templates/op/principal.html',
      controller: 'OpPrincipalCtrl'
    .state 'main.charge',
      url: '/charge',
      templateUrl: 'templates/op/charge.html',
      controller: 'OpChargeCtrl'
    .state 'main.verification_code',
      url: '/verification_code',
      templateUrl: 'templates/op/verification_code.html',
      controller: 'OpVerificationCtrl'
    .state 'main.phone_management',
      url: '/phone_management',
      templateUrl: 'templates/op/manage_by_phone.html',
      controller: 'OpPhoneManagementCtrl'
    .state 'main.phone_management.phone',
      url: '/phone/:phone',
      templateUrl: 'templates/admin/adult_manage.html',
      controller: 'OpShowPhoneCtrl'
    .state 'main.phone_management.teacher',
      url: '/teacher/:phone',
      templateUrl: 'templates/admin/teacher_manage.html',
      controller: 'OpShowTeacherCtrl'

    .state 'main.logging',
      url: '/logging',
      templateUrl: 'templates/op/logging.html',
      controller: 'OpLoggingMonitorCtrl'
    .state 'main.chat',
      url: '/chat',
      templateUrl: 'templates/op/chat.html',
      controller: 'OpChatCtrl'
    .state 'main.device',
      url: '/device',
      templateUrl: 'templates/op/verification_device.html',
      controller: 'OpDeviceCtrl'

    .state 'main.report',
      url: '/report',
      templateUrl: 'templates/op/reporting.html',
      controller: 'OpReportingCtrl'
    .state 'main.school_report',
      url: '/school_report/:school_id',
      templateUrl: 'templates/op/school_reporting.html',
      controller: 'OpSchoolReportingCtrl'

    .state 'main.score',
      url: '/score',
      templateUrl: 'templates/op/score_dashboard.html',
      controller: 'OpTeacherScoreCtrl'

    $urlRouterProvider.otherwise ($injector, $location) ->
      $location.path '/main/school'


]

angular.module("kulebaoOp").config ($modalProvider) ->
  angular.extend $modalProvider.defaults,
    animation: 'am-fade'
    placement: 'center'
    backdrop: 'static'

angular.module("kulebaoOp").config ($alertProvider) ->
  angular.extend $alertProvider.defaults,
    animation: 'am-fade-and-slide-top'
    placement: 'top'
    type: "danger"
    show: true
    container: '.main-view'
    duration: 3