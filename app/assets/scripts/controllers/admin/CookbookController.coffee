'use strict'

angular.module('kulebaoAdmin')
.controller 'CookbookCtrl', [ '$scope', '$rootScope', '$stateParams',
                              'cookbookService', 'uiCalendarConfig',
  (scope, rootScope, stateParams, Cookbook, Config) ->
    rootScope.tabName = 'cookbook'
    scope.cookbook_changed = false
    scope.isEditing = false
    rootScope.loading = true
    scope.cookbooks = Cookbook.bind(school_id: stateParams.kindergarten).query ->
      scope.cookbook = scope.cookbooks[0]
      unless scope.cookbook?
        scope.cookbook = new Cookbook
          school_id: parseInt stateParams.kindergarten
      rootScope.loading = false

      scope.$watch 'cookbook', (oldv, newv) ->
        scope.cookbook_changed = true if newv isnt oldv
      , true

    scope.toggleEditing = (e) ->
      e.stopPropagation()
      scope.isEditing = !scope.isEditing
      console.log 'scope.cookbook changed: ' + scope.cookbook_changed
      if scope.cookbook_changed
        rootScope.loading = true
        scope.cookbook.$save ->
          scope.cookbook_changed = false
          rootScope.loading = false

    date = new Date();
    d = date.getDate();
    m = date.getMonth();
    y = date.getFullYear();
    scope.events = [
      {title: '早餐:荷包蛋，鹌鹑蛋，牛肉饼', start: new Date(y, m, d, 9)},
      {title: '午餐:兰州拉面，京酱肉丝', start: new Date(y, m, d, 12)},
      {title: '加餐:兰州烧饼', start: new Date(y, m, d, 15)},
      {title: '晚餐:土豆西红柿，野菜炖蘑菇', start: new Date(y, m, d, 17)},
    ]
    scope.eventsF = (start, end, timezone, callback) ->
      s = new Date(start).getTime() / 1000
      e = new Date(end).getTime() / 1000
      m = new Date(start).getMonth()
      callback [ {
        title: 'Feed Me ' + m
        start: s + 50000
        end: s + 100000
        allDay: false
        className: [ 'customFeed' ]
      } ] if callback?

    scope.eventSources = [
      scope.events, scope.eventsF
    ];

    scope.uiConfig = calendar:
      editable: true
      header:
        left: 'today prev,next'
        center: 'title'
        right: 'basicWeek basicDay month'
      eventClick: scope.eventOnClick
      dayClick: scope.dayOnClick

    scope.eventOnClick = (date, jsEvent, view) ->
      console.log date, jsEvent, view
    scope.dayOnClick = (date, jsEvent, view) ->
      console.log date, jsEvent, view

    rootScope.loading = false
]
