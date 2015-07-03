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
  ['kulebaoAdmin', 'ngCsv'])
.config ['$stateProvider', '$urlRouterProvider', '$compileProvider', ($stateProvider, $urlRouterProvider, $compileProvider) ->
    $stateProvider
    .state 'main',
      url: '/main/:agent_id'
      templateUrl: 'templates/agent/main.html'
      controller: 'AgentCtrl'
      resolve:
        currentAgent:
          (agentManagementService, $stateParams) ->
            agentManagementService.get(id: $stateParams.agent_id).$promise
        loggedUser:
          (employeeService, session, agentService) ->
            if (!isNaN(parseInt session.id)) then employeeService.get().$promise else agentService.get().$promise

    .state 'main.school',
      url: '/school'
      templateUrl: 'templates/agent/school.html'
      controller: 'AgentSchoolCtrl'
    .state 'main.commercial',
      url: '/commercial'
      templateUrl: 'templates/agent/commercial.html'
      controller: 'AgentCommercialCtrl'

    .state 'error',
      url: '/error'
      templateUrl: 'templates/agent/404.html'
      controller: 'AgentErrorCtrl'
      resolve:
        loggedUser:
          (employeeService) ->
            employeeService.get().$promise

    $urlRouterProvider.otherwise ($injector, $location) ->
      $location.path '/error'

    $compileProvider.debugInfoEnabled(true)

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