// Generated by CoffeeScript 1.6.3
(function() {
  var Config;

  Config = (function() {
    function Config($stateProvider, $urlRouterProvider) {
      $stateProvider.state('kindergarten', {
        url: '/kindergarten/:kindergarten',
        templateUrl: 'templates/admin/kindergarten_manage.html',
        controller: 'KgManageCtrl'
      }).state('kindergarten.bulletin', {
        url: '/bulletin',
        templateUrl: 'templates/admin/bulletin_manage.html',
        controller: 'BulletinManageCtrl'
      }).state('kindergarten.news', {
        url: '/news/:news',
        templateUrl: 'templates/admin/news_edit.html',
        controller: 'NewsEditCtrl'
      }).state('kindergarten.wip', {
        url: '/wip',
        template: '<div>Sorry, we are still in Building...</div><image class="img-responsive" src="assets/images/wip.gif"></image>',
        controller: 'WipCtrl'
      });
      $urlRouterProvider.otherwise(function($injector, $location) {
        var path;
        path = $location.path();
        if (path.indexOf("kindergarten", 0) < 0) {
          return $location.path('/kindergarten/school23');
        } else {
          return $location.path(path.replace(/(kindergarten\/[^\/]+)\/.+$/g, '$1/wip'));
        }
      });
    }

    return Config;

  })();

  window.kulebaoApp = "admin";

  angular.module(window.kulebaoApp, ['ui.router', 'ngResource', 'ngRoute', 'ui.bootstrap']).config(['$stateProvider', '$urlRouterProvider', Config]);

}).call(this);
