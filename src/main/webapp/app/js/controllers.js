var reportsApp = angular.module('reportsApp', []);

reportsApp.controller('ReportLauncherCtrl', function ($scope) {
    $scope.uniqueusers = [
        {'name': 'All users',
            'file': 'users_all.json'}
    ];
});
