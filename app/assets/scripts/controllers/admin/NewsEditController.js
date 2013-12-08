// Generated by CoffeeScript 1.6.3
(function() {
  'use strict';
  var Controller;

  Controller = (function() {
    function Controller($stateParams, newsService) {
      var _this = this;
      this.kindergarten = {
        id: 1,
        name: 'school23'
      };
      this.adminUser = {
        id: 1,
        name: '豆瓣'
      };
      this.readCount = 100;
      this.showEditBox = false;
      this.news = newsService.get({
        kg: this.kindergarten.name,
        news_id: $stateParams.news
      });
      this.backupContent = this.news.content;
      this.startEditing = function(e) {
        _this.backupContent = _this.news.content;
        e.stopPropagation();
        return _this.showEditBox = true;
      };
      this.cancelEditing = function(e) {
        e.stopPropagation();
        _this.news.content = _this.backupContent;
        return _this.showEditBox = false;
      };
      this.save = function(e) {
        e.stopPropagation();
        _this.showEditBox = false;
        if (_this.backupContent !== _this.news.content) {
          return _this.news.$save({
            kg: _this.kindergarten.name,
            news_id: _this.news.id
          });
        }
      };
      this.publish = function(news) {
        news.pushlished = true;
        return news.$save({
          kg: _this.kindergarten.name,
          news_id: _this.news.id
        });
      };
      this.hidden = function(news) {
        news.pushlished = false;
        return news.$save({
          kg: _this.kindergarten.name,
          news_id: _this.news.id
        });
      };
    }

    return Controller;

  })();

  angular.module(window.kulebaoApp).controller('NewsEditCtrl', ['$stateParams', 'newsService', Controller]);

}).call(this);
