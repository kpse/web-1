class Config
  constructor: ($stateProvider, $urlRouterProvider) ->
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

    .state('kindergarten.wip',
        url: '/wip',
        template: '<div>Sorry, we are still in Building...</div><image class="img-responsive" src="assets/images/wip.gif"></image>',
        controller: 'WipCtrl'
      )

    $urlRouterProvider.otherwise ($injector, $location) ->
      $location.path '/password'

angular.module('kulebaoApp', ['ui.router', 'ngResource', 'ngRoute', 'angulartics', 'angulartics.google.analytics']).config ['$stateProvider', '$urlRouterProvider', Config]
