angular.module('kulebaoApp', ['ui.router', 'ngResource', 'ngRoute', 'angulartics', 'angulartics.google.analytics'])
angular.module('kulebaoAdmin',
  ['kulebaoApp', 'ui.router', 'ngResource', 'ngRoute', 'ui.bootstrap', 'ui.mask', 'angulartics',
   'angulartics.google.analytics', 'ngCookies'])
angular.module('kulebaoOp',
  ['kulebaoAdmin', 'kulebaoApp', 'ui.router', 'ngResource', 'ngRoute', 'ui.bootstrap', 'ui.mask', 'angulartics',
   'angulartics.google.analytics', 'ngCookies', 'ngAnimate', 'ngSanitize', 'mgcrea.ngStrap'])
.config ['$stateProvider', '$urlRouterProvider',
  ($stateProvider, $urlRouterProvider) ->
    $stateProvider
    .state 'main',
      url: '/main',
      templateUrl: 'templates/op/main.html',
      controller: 'OpCtrl'
    .state 'main.app',
      url: '/app',
      templateUrl: 'templates/op/app.html',
      controller: 'OpAppCtrl'
    .state 'main.school',
      url: '/school',
      templateUrl: 'templates/op/school.html',
      controller: 'OpSchoolCtrl'
    .state 'main.feedback',
      url: '/feedback',
      templateUrl: 'templates/op/feedback.html',
      controller: 'OpFeedbackCtrl'
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