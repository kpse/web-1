angular.module('kulebao.services', ['ngResource'])
angular.module('kulebao.directives', ['kulebao.services'])
angular.module('kulebao.filters', ['kulebao.services'])
angular.module('kulebaoApp',
  ['ui.router', 'ngResource', 'ngRoute', 'angulartics', 'angulartics.google.analytics', 'kulebao.directives',
   'kulebao.filters'])
angular.module('kulebaoAdmin',
  ['kulebaoApp', 'ui.mask', 'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap',
   'mgcrea.ngStrap.helpers.dimensions',
   'ngCookies'])
angular.module('kulebaoOp',
  ['kulebaoAdmin', 'emoji', 'ngCsv'])
.config ['$stateProvider', '$urlRouterProvider', '$compileProvider',
  ($stateProvider, $urlRouterProvider, $compileProvider) ->
    $stateProvider
    .state 'main',
      url: '/main',
      templateUrl: 'templates/op/main.html',
      controller: 'OpCtrl'
      resolve:
        AdminUser: (employeeService) -> employeeService.get().$promise
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
    .state 'main.phone_management.card',
      url: '/card/:card',
      templateUrl: 'templates/admin/card_manage.html',
      controller: 'OpShowCardCtrl'

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

    .state 'main.video',
      url: '/video',
      templateUrl: 'templates/op/video_dashboard.html',
      controller: 'OpVideoMemberCtrl'
    .state 'main.video_school',
      url: '/video_in_school/:school_id',
      templateUrl: 'templates/op/video_school_detail.html',
      controller: 'OpVideoMemberInSchoolCtrl'
    .state 'main.video_school.class',
      url: '/class/:class_id',
      templateUrl: 'templates/op/video_class_detail.html',
      controller: 'OpVideoMemberInClassCtrl'

    .state 'main.agent',
      url: '/agent',
      templateUrl: 'templates/op/agent_dashboard.html',
      controller: 'OpAgentManagementCtrl'


    .state 'main.ad',
      url: '/commercials',
      templateUrl: 'templates/op/ad_dashboard.html',
      controller: 'OpAdsCtrl'
    .state 'main.ad_all',
      url: '/all_commercials',
      templateUrl: 'templates/op/ad_all.html',
      controller: 'OpAllAdsCtrl'
    .state 'main.ad_school',
      url: '/ad_in_school/:school_id',
      templateUrl: 'templates/op/ad_school_detail.html',
      controller: 'OpAdsInSchoolCtrl'
    .state 'main.ad_school.position',
      url: '/position/:position_id',
      templateUrl: 'templates/op/ad_position_detail.html',
      controller: 'OpAdPositionCtrl'

    $urlRouterProvider.otherwise ($injector, $location) ->
      $location.path '/main/school'

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