var myeltAnalyticsControllers = angular.module('myeltAnalyticsControllers', []);

myeltAnalyticsControllers.controller('ReportsController', ['$scope', '$http',
     function ($scope, $http) {
        $scope.reports = {
            "uniqueUsers": {
                "title": "Unique Users",
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
                "title": "Registrations",
                "list": [{
                    "name": "Registrations FY11",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Registrations FY12",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Registrations FY13",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Registrations FY14 (Current)",
                    "disabled": "true",
                    "file": ""
                }]
            },
            "usersByInstitute": {
                "title": "Users by Institutes / Customers",
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
                    "name": "All Access codes",
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
                }, {
                    "name": "Access codes activated by product",
                    "file": "accesscodes_by_product.json"
                }]
            },
            "capes": {
                "title": "CAPES Reports",
                "list": [{
                    "name": "All CAPES Model(OCC etc.)",
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
                "title": "Completed Activities",
                "list": [{
                    "name": "All Activities and Assignments",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Assignments only",
                    "disabled": "true",
                    "file": ""
                }, {
                    "name": "Examview",
                    "disabled": "true",
                    "file": ""
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

                   $('<div class="expander"></div>').appendTo(this.inner).click(function () {
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

myeltAnalyticsControllers.controller('AdminController', ['$scope','$http',
   function ($scope, $http) {
     $http.get('../admin/mysqlinfo').success(function(data) {
        $scope.mysqlInfo = data;
     });     
   }
]);

