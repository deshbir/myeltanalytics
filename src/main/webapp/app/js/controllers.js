var myeltAnalyticsControllers = angular.module('myeltAnalyticsControllers', []);

myeltAnalyticsControllers.controller('ReportsController', ['$scope', '$http',
     function ($scope, $http) {
        $scope.reports = {
            "uniqueUsers": {
                "title": "All Users",
                "list": [{
                    "name": "All users",
                    "file": "users_all.json"
                }, {
                    "name": "Users by product",
                    "file": "users_by_product.json"
                }, {
                    "name": "Users by country / region",
                    "file": "users_by_country.json"
                }, {
                    "name": "Self-paced vs instructor-led",
                    "file": "users_by_learning_model.json"
                }]
            },
            "registrations": {
                "title": "New User Registrations",
                "list": [{
                    "name": "Registrations FY14 (Current)",
                    "file": "users_fy14.json"
                }, {
                    "name": "Registrations FY13",
                    "file": "users_fy13.json"
                }, {
                    "name": "Registrations FY12",
                    "file": "users_fy12.json"
                }, {
                    "name": "Registrations FY11",
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
                    "name": "AACR FY11",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "AACR FY12",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "AACR FY13",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "AACR FY14 (Current)",
                    "disabled": "true",
                    "file": ""
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
                "title": "Activities",
                "list": [{
                    "name": "All Activities and Assignments",
                    "file": "submissions_all.json"
                }, {
                    "name": "Assignments only",
                    "file": "submissions_assignments.json"
                }, {
                    "name": "Examview",
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

myeltAnalyticsControllers.controller('SettingsController', ['$scope','$http',
   function ($scope, $http) {
     $http.get('../settings/mysqlinfo').success(function(data) {
        $scope.mysqlInfo = data;
     });     
   }
]);

myeltAnalyticsControllers.controller('RulesController', ['$scope','$http',
    function ($scope, $http) {
        $http.get('../rules/regionmap').success(function(data) {
            $scope.regionmap = data;
        });  
        $http.get('../rules/ignoreinstitutions').success(function(data) {
            $scope.ignoreinstitutions = data;
        });  
    }
 ]);

