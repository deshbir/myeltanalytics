var myeltAnalyticsApp = angular.module('myeltAnalyticsApp', ['ngRoute','myeltAnalyticsControllers']);


myeltAnalyticsApp.config(['$routeProvider',
    function($routeProvider) {
      $routeProvider.
        when('/reports', {
          templateUrl: 'partials/reports.html',
          controller: 'ReportsController'
        }).
        when('/sync', {
            templateUrl: 'partials/sync.html',
            controller: 'SyncController',
            resolve : {
            	 jsonUserSyncData : function(getDataFromServerService , $http){
                	return getDataFromServerService.getData($http , '../sync/users/status');
                },
                jsonSubmissionSyncData : function(getDataFromServerService , $http){
		        	return getDataFromServerService.getData($http , '../sync/submissions');
		        }
            }
          }).
        when('/settings', {
            templateUrl: 'partials/settings.html',
            controller: 'SettingsController',
            resolve : {
            	jsonDataSetting : function(getDataFromServerService , $http){
                	return getDataFromServerService.getData($http , '../settings/mysqlinfo');
                }
            }
        }).
        when('/rules', {
            templateUrl: 'partials/rules.html',
            controller: 'RulesController',
            resolve : {
            	jsonDataRules : function(getDataFromServerService , $http){
                	return getDataFromServerService.getData($http , '../rules/regionmap');
                },
        		jsonDataIgnoreInstitutions : function(getDataFromServerService , $http){
		        	return getDataFromServerService.getData($http , '../rules/ignoreinstitutions');
		        }
            }
        }).
        when('/reports/myeltUsage/:year/:month', {
            templateUrl: 'partials/myeltUsageReport.html',
            controller: 'MyELTUsageReportController',
            resolve: {
                reportData: function(getMyELTUsageReportDataService,$route,$http){
                	return getMyELTUsageReportDataService.getData($http , "../api/reports/myeltusage/"+$route.current.params.year+"/"+$route.current.params.month);
                }
            }
        }).
        otherwise({
          redirectTo: '/reports'
        });
    }
]);

myeltAnalyticsApp.factory("getMyELTUsageReportDataService", function(){
    return {
    	getData: function($http,url){
            showLoader();
    		var result = $http.get(url).success(function(data){
            				return data;
            });
            return result;
    	}
    };
});

myeltAnalyticsApp.factory("getDataFromServerService", function(){
    return {
    	getData: function($http,url){
            showLoader();
    		var result = $http.get(url,{cache:true}).success(function(data){
            				return data;
            });
            return result;
    	}
    };
});