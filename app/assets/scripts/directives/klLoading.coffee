'use strict'

angular.module("kulebao.directives").directive "klLoading",
  ->
    return (
      restrict: "A"
      scope:
        loading: "=?klLoading"
      templateUrl: '/templates/directives/kl_loading_spinner.html'
    )