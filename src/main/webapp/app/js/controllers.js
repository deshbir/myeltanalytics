var myeltAnalyticsControllers = angular.module('myeltAnalyticsControllers', []);

myeltAnalyticsControllers.controller('ReportsController', ['$scope', '$http',
     function ($scope, $http) {
        $http.get('json/reports.json').success(function(data) {
           $scope.reports = data;
        });     
    
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