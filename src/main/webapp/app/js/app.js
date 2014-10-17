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
        when('/reports/myeltUsage/:year/:month', {
            templateUrl: 'partials/myeltUsageReport.html',
            controller: 'MyELTUsageReportController'
        }).
        otherwise({
          redirectTo: '/reports'
        });
    }
]);
