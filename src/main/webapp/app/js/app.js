var myeltAnalyticsApp = angular.module('myeltAnalyticsApp', ['ngRoute','myeltAnalyticsControllers']);


myeltAnalyticsApp.config(['$routeProvider',
    function($routeProvider) {
      $routeProvider.
        when('/reports', {
          templateUrl: 'partials/reports.html',
          controller: 'ReportsController'
        }).
        when('/settings', {
            templateUrl: 'partials/settings.html',
            controller: 'SettingsController'
        }).
        when('/rules', {
            templateUrl: 'partials/rules.html',
            controller: 'RulesController'
        }).
        otherwise({
          redirectTo: '/reports'
        });
    }
]);
