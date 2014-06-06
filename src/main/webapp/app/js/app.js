var myeltAnalyticsApp = angular.module('myeltAnalyticsApp', ['ngRoute','myeltAnalyticsControllers']);


myeltAnalyticsApp.config(['$routeProvider',
    function($routeProvider) {
      $routeProvider.
        when('/reports', {
          templateUrl: 'partials/reports.html',
          controller: 'ReportsController'
        }).
        when('/admin', {
            templateUrl: 'partials/admin.html',
            controller: 'AdminController'
        }).
        otherwise({
          redirectTo: '/reports'
        });
    }
]);
