angular.module('PmoxApp')
// Creating the Angular Controller
    .controller('NavController', function ($http, $scope, AuthService, $state, $rootScope) {
        $scope.$on('LoginSuccessful', function () {
            $scope.user = AuthService.user;
        });
        $scope.$on('LogoutSuccessful', function () {
            $scope.user = null;
        });
        $scope.logout = function () {
            AuthService.user = null;
            $rootScope.$broadcast('LogoutSuccessful');
            $state.go('login');
        };
        $scope.scrollTo = function (scrn) {
          alert(scrn);
          $state.go(scrn);
      };
      
      $scope.clickNavBar = function ()
      {
        alert(6458937349)
        AuthService.makeShrink = 'shrink';
        $rootScope.$broadcast('makeShrink');
        alert(645893734900000)
        //$state.go(scrn);
      };
      
    });
