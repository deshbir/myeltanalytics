var myeltAnalyticsApp = angular.module('myeltAnalyticsApp', ['ngRoute','myeltAnalyticsControllers']);


myeltAnalyticsApp.config(['$routeProvider',
    function($routeProvider) {
      $routeProvider.
        when('/reports', {
          templateUrl: 'partials/reports.html',
          controller: 'ReportsController'
        }).
        otherwise({
          redirectTo: '/reports'
        });
    }
]);
