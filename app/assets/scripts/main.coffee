angular.module('kulebao.services', ['ngResource'])
angular.module('kulebao.directives', ['kulebao.services'])
angular.module('kulebao.filters', ['kulebao.services'])
angular.module('kulebaoApp', ['ui.router', 'ngResource', 'ngRoute', 'angulartics', 'angulartics.google.analytics', 'mgcrea.ngStrap', 'kulebao.directives', 'kulebao.filters'])
.config ['$stateProvider', '$urlRouterProvider',
  ($stateProvider, $urlRouterProvider) ->
    $stateProvider
    .state('kindergarten',
      url: '/kindergarten/:kindergarten',
      templateUrl: 'templates/kindergarten.html',
      controller: 'KindergartenCtrl'
    )
    .state('kindergarten.bulletin',
      url: '/bulletin',
      templateUrl: 'templates/bulletin.html',
      controller: 'BulletinCtrl'
    )
    .state('kindergarten.news',
      url: '/news/:news',
      templateUrl: 'templates/news.html',
      controller: 'NewsCtrl'
    )
    .state('forgotten',
      url: '/password',
      templateUrl: 'templates/forgotten_password.html',
      controller: 'ForgottenCtrl'
    )
    .state('reset_password',
      url: '/reset/:phone',
      templateUrl: 'templates/reset_password.html',
      controller: 'PasswordCtrl'
    )

    .state('kindergarten.wip',
      url: '/wip',
      template: '<div>Sorry, we are still in Building...</div><image class="img-responsive" src="assets/images/wip.gif"></image>',
      controller: 'WipCtrl'
    )

    $urlRouterProvider.otherwise ($injector, $location) ->
      $location.path '/password'

]

angular.module("kulebaoApp").config ($modalProvider) ->
  angular.extend $modalProvider.defaults,
      animation: 'am-fade'
      placement: 'center'
      backdrop: 'static'