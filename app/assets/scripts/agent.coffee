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
angular.module('kulebaoAgent',
  ['kulebaoAdmin'])
.config ['$stateProvider', '$urlRouterProvider', '$compileProvider',
  ($stateProvider, $urlRouterProvider, $compileProvider) ->
    $stateProvider
    .state 'main',
      url: '/main',
      templateUrl: 'templates/agent/main.html',
      controller: 'AgentCtrl'
      resolve:
        AdminUser: (agentService) -> agentService.get().$promise
    .state 'main.school',
      url: '/school',
      templateUrl: 'templates/agent/school.html',
      controller: 'AgentSchoolCtrl'

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