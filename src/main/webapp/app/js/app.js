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
            controller: 'MyELTUsageReportController',
            resolve: {
                reportData: function(myeltUsageReportDataService,$route,$http){
                	return myeltUsageReportDataService.getReportData($route,$http);
                }
            }
        }).
        otherwise({
          redirectTo: '/reports'
        });
    }
]);

myeltAnalyticsApp.factory("myeltUsageReportDataService", function(){
    return {
    	getReportData: function($route,$http){
            showLoader();
    		var reportData = $http.get("../api/reports/myeltusage/"+$route.current.params.year+"/"+$route.current.params.month).success(function(data){
            				return data;
            });
            return reportData;
    	}
    };
});
