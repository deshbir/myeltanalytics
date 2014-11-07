var myeltAnalyticsControllers = angular.module('myeltAnalyticsControllers', []);
var monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
myeltAnalyticsControllers.controller('ReportsController', ['$scope', '$http','$q',
     function ($scope, $http,$q) {
	    var currentMonth = new Date(),
	    	lastMonth = new Date(),
	    	secondLastMonth = new Date(),
	    	thirdLastMonth = new Date(),
	    	fourthLastMonth	= new Date(),
	    	fifthLastMonth= new Date();
	    currentMonth.setDate(1);
	    lastMonth.setDate(1);
	    secondLastMonth.setDate(1);
	    thirdLastMonth.setDate(1);
	    fifthLastMonth.setDate(1);
	    lastMonth.setMonth(currentMonth.getMonth() - 1);
		secondLastMonth.setMonth(currentMonth.getMonth() - 2);
		thirdLastMonth.setMonth(currentMonth.getMonth() - 3);
		fourthLastMonth.setMonth(currentMonth.getMonth() - 4);
		fifthLastMonth.setMonth(currentMonth.getMonth() - 5);
		$scope.reports = {
            "uniqueUsers": {
                "title": "Accounts",
                "list": [{
                    "name": "All Accounts",
                    "file": "users_all.json"
                }, {
                    "name": "Accounts by product",
                    "file": "users_by_product.json"
                }, {
                    "name": "Accounts by country / region",
                    "file": "users_by_country.json"
                }, {
                    "name": "Self-paced vs instructor-led",
                    "file": "users_by_learning_model.json"
                }]
            },
            "registrations": {
                "title": "New Accounts Created",
                "list": [{
                    "name": "FY14 (Current)",
                    "file": "users_fy14.json"
                }, {
                    "name": "FY13",
                    "file": "users_fy13.json"
                }, {
                    "name": "FY12",
                    "file": "users_fy12.json"
                }, {
                    "name": "FY11",
                    "file": "users_fy11.json"
                }]
            },
            "usersByInstitute": {
                "title": "Institutes / Customers",
                "list": [{
                    "name": "CAPES",
                    "file": "users_capes.json"
                }, {
                    "name": "ELTeach",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "ICPNA",
                    "file": "users_icpna.json"
                }, {
                    "name": "eStudy",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Seven",
                    "file": "users_seven.json"
                }]
            },
            "accessCodes": {
                "title": "Activated Access Codes",
                "list": [{
                    "name": "All Activated Access codes",
                    "file": "accesscodes_all.json"
                }, {
                    "name": "AACR FY14 (Current)",
                    "file": "accesscodes_fy14.json"
                }, {
                    "name": "AACR FY13",
                    "file": "accesscodes_fy13.json"
                }, {
                    "name": "AACR FY12",
                    "file": "accesscodes_fy12.json"
                }, {
                    "name": "AACR FY11",
                    "file": "accesscodes_fy11.json"
                }]
            },
            "capes": {
                "title": "CAPES",
                "list": [{
                    "name": "All CAPES (OCC etc.)",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "CAPES (Brazil)",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Users changed levels",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Users had not started level after placement test",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Average time spent per level",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Users without access for more than 30 days",
                    "disabled": "true",
                    "file": ""
                }]
            },
            "completedActivities": {
                "title": "Submissions",
                "list": [{
                    "name": "All Submissions",
                    "file": "submissions_all.json"
                }, {
                    "name": "Assignments only",
                    "file": "submissions_assignments.json",
                    "disabled": "true",
                },
                {
                    "name": "Activities only",
                    "file": "",
                    "disabled": "true",
                },{
                    "name": "Examview only",
                    "file": "submissions_examview.json"
                }]
            },
            "submissions": {
                "title": "Submissions",
                "list": [{
                    "name": "Submissions FY11",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Submissions FY12",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Submissions FY13",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Submissions FY14 (Current)",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Grades posted VS activity",
                    "disabled": "true",
                    "file": ""
                }]
            },
            "myeltUsageReport": {
            	"title" : "MyELT Usage Report",
            	"list": [{
            		"name" : monthNames[currentMonth.getMonth()] +" '" +currentMonth.getFullYear().toString().slice(-2),
            		"disabled": "false",
            		"file": "",
            		"period": currentMonth.getFullYear()+"/"+ (currentMonth.getMonth() +1)
            	},{
            		"name" : monthNames[lastMonth.getMonth()] +" '" +lastMonth.getFullYear().toString().slice(-2),
            		"disabled": "false",
            		"file": "",
            		"period": lastMonth.getFullYear()+"/"+ (lastMonth.getMonth() + 1)
            	},{
            		"name" : monthNames[secondLastMonth.getMonth()] +" '" +secondLastMonth.getFullYear().toString().slice(-2),
            		"disabled": "false",
            		"file": "",
            		"period": secondLastMonth.getFullYear() +"/"+ (secondLastMonth.getMonth() + 1)
            	},{
            		"name" : monthNames[thirdLastMonth.getMonth()] +"'" +thirdLastMonth.getFullYear().toString().slice(-2),
            		"disabled": "false",
            		"file": "",
            		"period": thirdLastMonth.getFullYear()+"/"+ (thirdLastMonth.getMonth()+1)
            	},{
            		"name" : monthNames[fourthLastMonth.getMonth()] +" '" +fourthLastMonth.getFullYear().toString().slice(-2),
            		"disabled": "false",
            		"file": "",
            		"period": fourthLastMonth.getFullYear()+"/"+ (fourthLastMonth.getMonth() + 1)
            	},{
            		"name" : monthNames[fifthLastMonth.getMonth()] +" '" +fifthLastMonth.getFullYear().toString().slice(-2),
            		"disabled": "false",
            		"file": "",
            		"period": fifthLastMonth.getFullYear() +"/"+ (fifthLastMonth.getMonth() + 1)
            	}]
            }

        };
        $scope.openReport = function (filename) {
           var iframeWidth = $(window).width() - 100;
           var iframeHeight = $(window).height() - 100;
           var fullURL = "../reports/index.html#/dashboard/file/" + filename;

           $.fancybox({
               href: fullURL,
               type: "iframe",
               autoSize: false,
               minWidth: iframeWidth,
               minHeight: iframeHeight,
               keys: {
                   close: null //disable escape
               },
               afterShow: function () {
                   //Add a fullscreen button -- Improve this

                   $('<div class="fancybox-fullscreen"></div>').appendTo(this.skin).click(function () {
                       //Hook iframe content to jquery.fullscreen.js
                       $('iframe').contents().toggleFullScreen();
                   });
               },
               afterClose: function () {
                   $('iframe').contents().fullScreen(false);
               }
           })
       }

       $('iframe').contents().bind("fullscreenerror", function () {
           alert("Browser rejected fullscreen");
       });

}]);

myeltAnalyticsControllers.controller('NavigationController', ['$scope', '$location',
    function ($scope, $location) {
       $scope.isActive = function (viewLocation) {
            var active = (viewLocation == $location.path());
            return active;
       };
    }
]);

myeltAnalyticsControllers.controller('SettingsController', function ($scope , jsonDataSetting) {
        $scope.mysqlInfo = jsonDataSetting.data;
        hideLoader();
   }
);

myeltAnalyticsControllers.controller('RulesController', function ($scope , jsonDataRules , jsonDataIgnoreInstitutions) {
            $scope.regionmap = jsonDataRules.data;
            $scope.ignoreinstitutions = jsonDataIgnoreInstitutions.data;
            hideLoader();
});

myeltAnalyticsControllers.controller('SyncController', function ($scope ,$http,$interval,jsonUserSyncData , jsonSubmissionSyncData) {
    $scope.usersJobInfo = jsonUserSyncData.data;
    hideLoader();
    $scope.manaageUsersSynButton=function(){
		if($scope.usersJobInfo.jobStatus == "InProgress"){
			$scope.usersSpinClass = "fa-spin";
			$scope.usersStartButtonDisplay = "hide";
			$scope.usersResumeButtonDisplay = "hide";
			$scope.usersStopButtonDisplay = "";
			$scope.usersBadgeClass = "";
			$scope.failedUsersSyncInProcessButtonDisplay="hide";
			if(fetchSyncPoller == null){
				triggerFetchingSyncStatus();
			}
		}else if($scope.usersJobInfo.jobStatus == "Completed"){
			$scope.usersSpinClass = "";
			$scope.usersStartButtonDisplay = "";
			$scope.usersResumeButtonDisplay = "hide";
			$scope.usersStopButtonDisplay = "hide";
			$scope.usersBadgeClass = "badge-success";
			
			$scope.failedUsersSyncInProcessButtonDisplay="hide";
			if ( $scope.usersJobInfo.errorRecords > 0) {
          	  $("#userFailedRecord i.fa-spin").hide();
          	  $("#userFailedRecord i.fa-repeat").show();
            }
            abortFetchingSyncStatus();
		}else if($scope.usersJobInfo.jobStatus == "Paused"){
			$scope.usersBadgeClass = "badge-error";
			$scope.usersSpinClass = "";
			$scope.usersStartButtonDisplay = "";
			$scope.usersResumeButtonDisplay = "";
			$scope.usersStopButtonDisplay = "hide";
			if($scope.usersJobInfo.retryJobStatus == "Completed"){
				if ( $scope.usersJobInfo.errorRecords > 0) {
	                  $("#userFailedRecord i.fa-repeat").show();
	                  $("#userFailedRecord i.fa-spin").hide();
                } else {
              	  $("#userFailedRecord i.fa-repeat").hide();
	                  $("#userFailedRecord i.fa-spin").hide();
                }
                abortFetchingSyncStatus();
			}
			$scope.failedUsersSyncInProcessButtonDisplay="hide";
		}else if($scope.usersJobInfo.jobStatus == "InProgressRetry"){
		
			$scope.usersStartButtonDisplay="hide";
			$scope.usersResumeButtonDisplay="hide";
			$scope.usersStopButtonDisplay="";
			$scope.failedUsersSyncButtonDisplay="hide";
			$scope.failedUsersSyncInProcessButtonDisplay=""; 
		}
		else{
			$scope.usersBadgeClass = "badge-info";
			$scope.usersActiveClass = "active";
			$scope.usersStartButtonDisplay = "";
			$scope.usersResumeButtonDisplay = "hide";
			$scope.usersStopButtonDisplay = "hide";
			$scope.failedUsersSyncInProcessButtonDisplay="hide";
		}
		if($scope.usersJobInfo.errorRecords > 0 && !($scope.usersStartButtonDisplay == "hide")){
			$scope.failedUsersSyncButtonDisplay = "";
		}else{
			$scope.failedUsersSyncButtonDisplay = "hide";
		}
	};
	var fetchSyncPoller = null;
    triggerFetchingSyncStatus = function () {
        fetchSyncPoller = $interval(function(){
            $http.get("../sync/users/getSyncStatus").success(function(data){
              $scope.usersJobInfo  = data;
              $scope.manaageUsersSynButton();
            })
        }, 5000);
    }; 
    
    abortFetchingSyncStatus = function () {
        $interval.cancel(fetchSyncPoller);
    }; 
    
	  $scope.startSync= function () {
	        $("#usersStartButton").attr("disabled","disabled");
	        $("#usersResumeButton").attr("disabled","disabled");
	        $("#usersStartButton i.fa-play").hide();
	        $("#usersStartButton i.fa-spin").show();
	        $("#userFailedRecord i.fa-repeat").hide();
	        $http.get("../sync/users/start").success(function(data){
	            $scope.usersJobInfo=data;
	            if ($scope.usersJobInfo.status == "success") {
	                triggerFetchingSyncStatus();
		            $("#usersStartButton").removeAttr("disabled");
		            $("#usersResumeButton").removeAttr("disabled");
		            $("#usersStartButton i.fa-play").show();
		            $("#usersStartButton i.fa-spin").hide();
		            $scope.manaageUsersSynButton();
	            }
	        });
	    };
	    $scope.stopSync= function () {
	        abortFetchingSyncStatus();
	        $("#usersStopButton").attr("disabled","disabled");
	        $("#usersStopButton i.fa-stop").hide();
	        $("#usersStopButton i.fa-spin").show();
	        $http.get("../sync/users/stop").success(function(data){
	           $scope.usersJobInfo=data;
	           if ($scope.usersJobInfo.status == "success") {
	        	   if($scope.usersJobInfo.errorRecords > 0){
	        		   $("#userFailedRecord i.fa-repeat").show();
	        		   $("#userFailedRecord i.fa-spin").hide();
	        	   }
		           $("#usersStopButton").removeAttr("disabled");
		           $("#usersStopButton i.fa-stop").show();
		           $("#usersStopButton i.fa-spin").hide();
		           $scope.manaageUsersSynButton();
	           }
	        });

	    };
	    	    
	    $scope.resumeSync= function () {
	        $("#usersStartButton").attr("disabled","disabled");
	        $("#usersResumeButton").attr("disabled","disabled");
	        $("#usersResumeButton i.fa-play-circle-o").hide();
	        $("#usersResumeButton i.fa-spin").show();
	        $("#userFailedRecord i.fa-repeat").hide();
	        $http.get("../sync/users/resume").success(function(data){
	            $scope.usersJobInfo = data;
	            $scope.usersJobInfo=data;
	            if ( $scope.usersJobInfo.status == "success") {
	                triggerFetchingSyncStatus();
		            $("#usersStartButton").removeAttr("disabled");
		            $("#usersResumeButton").removeAttr("disabled");
		            $("#usersResumeButton i.fa-play-circle-o").show();
		            $("#usersResumeButton i.fa-spin").hide();
		            $scope.manaageUsersSynButton();
	            }
	        });
	        
	    };
	    
	    $scope.retryFailedUsers = function () {
			$("#usersStartButton").attr("disabled","disabled");
	        $("#usersResumeButton").attr("disabled","disabled");
	        $http.get("../sync/users/retryFailed").success(function(data){
	        	$scope.usersJobInfo = data;
	            if ( $scope.usersJobInfo.status == "success") {
	            	triggerFetchingSyncStatus();
		            $("#usersStartButton").removeAttr("disabled");
		            $("#usersResumeButton").removeAttr("disabled");
		            $("#usersStartButton i.fa-play").show();
			        $("#userFailedRecord i.fa-repeat").hide();
			        $("#userFailedRecord i.fa-spin").show();
		            $scope.manaageUsersSynButton();
	            }
	        });
	    };
    
});
myeltAnalyticsControllers.controller("MyELTUsageReportController", function ($scope, $routeParams , reportData) {
	jQuery(window).scrollTop(0);
	$scope.year = $routeParams.year;
	$scope.month  = $routeParams.month;
	$scope.keys = [];
	for(var i  = 0; i < 13; i++){
		$scope.keys.push({"data" : monthNames[$scope.month -1]+" '"+$scope.year.toString().slice(-2)});
		if($scope.month == 1){
			$scope.month = 13;
			$scope.year--;
		}
		$scope.month--;
	}
	$scope.keys.reverse();
	$scope.keys.push({"data":"Last 12 Months"});
	$scope.usageReportData = reportData.data;
	$scope.newStudentsReg_capes = [];
	$scope.newStudentsReg_allOther = [];
	$scope.newStudentsReg_total = [];
	$scope.newProductsReg_capes = [];
	$scope.newProductsReg_allOther = [];
	$scope.newProductsReg_total = [];
	for(var j = 0 ; j < $scope.keys.length ; j++ ){
		$scope.newStudentsReg_capes.push({"data" :$scope.usageReportData.new_student_registrations.CAPES[$scope.keys[j].data]});
		$scope.newStudentsReg_allOther.push({"data":$scope.usageReportData.new_student_registrations.AllOther[$scope.keys[j].data]});
		$scope.newStudentsReg_total.push({"data":$scope.usageReportData.new_student_registrations.CAPES[$scope.keys[j].data]+$scope.usageReportData.new_student_registrations.AllOther[$scope.keys[j].data]});
		$scope.newProductsReg_capes.push({"data" :$scope.usageReportData.new_product_registrations.CAPES[$scope.keys[j].data]});
		$scope.newProductsReg_allOther.push({"data" :$scope.usageReportData.new_product_registrations.AllOther[$scope.keys[j].data]});
		$scope.newProductsReg_total.push({"data":$scope.usageReportData.new_product_registrations.CAPES[$scope.keys[j].data]+$scope.usageReportData.new_product_registrations.AllOther[$scope.keys[j].data]});
		if(j == $scope.keys.length - 2){
			$scope.activeUsers_capes = $scope.usageReportData.active_user_accounts.CAPES[$scope.keys[j].data];
			$scope.activeUsers_allOther = $scope.usageReportData.active_user_accounts.AllOther[$scope.keys[j].data];
			$scope.activeUsers_total = $scope.usageReportData.active_user_accounts.CAPES[$scope.keys[j].data]+$scope.usageReportData.active_user_accounts.AllOther[$scope.keys[j].data];
		}
	}
	hideLoader();
	$scope.printReport = function(){
		window.print();
	}
});
myeltAnalyticsControllers.directive('odometer',function () {
    return {
      restrict: 'E',
      scope : {
        endValue : '@value'
        	
      },
      link: function(scope, element) {
        // If you want to change the format, you have to add the necessary
        //  parameters. In this case I am going with the defaults.
        var od = new Odometer({
            el : element[0],
            value : 0   // default value
        });
        // update the odometer element when there is a 
        // change in the model value.
        scope.$watch('endValue', function() {
          od.update(scope.endValue);
        });
      }
    };
  });

function showLoader() {
	jQuery(".backgroundLoaderCover").show();
	var spinner = jQuery(".spinner");
	spinner.css("top", Math.max(0, (($(window).height() - spinner.outerHeight()) / 2) + 
            $(window).scrollTop()) + "px");
	spinner.css("left", Math.max(0, (($(window).width() - spinner.outerWidth()) / 2) + 
            $(window).scrollLeft()) + "px");
	spinner.show();
}

function hideLoader(){
		jQuery(".backgroundLoaderCover").hide();
		jQuery(".spinner").hide();
}
