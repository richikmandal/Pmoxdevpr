angular.module('PmoxApp')
// Creating the Angular Controller
    .controller('projectcontroller', function ($http, $scope, AuthService) {
    	
    	$scope.users=AuthService.user;
    	
    });