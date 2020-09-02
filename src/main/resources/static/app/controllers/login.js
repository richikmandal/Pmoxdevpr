angular.module('PmoxApp')
// Creating the Angular Controller
    .controller('LoginController', function ($http, $scope, $state, AuthService, $rootScope) {
    
    	 $scope.showdropdown=false;
         $scope.showlogin=true;
         $scope.showenter=false;
         $scope.disablelogin=false;
         $scope.selectedvalues="";
         var loginuser={};
        $scope.login = function () {
          $scope.loaderds = true;
          var authenticationRequest={};
          authenticationRequest.username= $scope.username;
          authenticationRequest.password=$scope.password;
          
          
          $.ajax({
            url: "api/authenticate",
            error: function (e) {
              // alert(JSON.stringify(e))
            },
            dataType: "json",
            contentType: 'application/json; charset=utf-8',
           // headers: {"Authorization": "Bearer "+AuthService.token},
            type: "POST",
            async: false,
            cache: false,
            data: JSON.stringify(authenticationRequest),
            timeout: 30000,
            crossDomain: true,
            success: function (res) {               
             
              $scope.password = null;
              
              //alert(JSON.stringify(res))

              if (res.user.username!="IncorrectUsernamePassword") {
               
                  $scope.message = '';
                  var user={};
                  var roles=[];
                  $scope.roleList=[]; 
                  roles=res.user.roleName.split(",");
                  
                 
                  $scope.roleList=[];
                  var arrayLength = roles.length;
                  for (var i = 0; i < arrayLength; i++) {
                     
                     var user = new Object();
                     user.id=roles[i];
                     user.role=roles[i];
                     $scope.roleList.push(user);
                      //Do something
                  }
                  //alert(JSON.stringify($scope.roleList));
                  var user = new Object();
                  user.id="Selct role";
                  user.role="Selct role";
                  $scope.roleList.push(user);
                  arrayLength = roles.length;
                  $scope.MyData=$scope.roleList[$scope.roleList.length -1];
                  //alert(JSON.stringify($scope.MyData));
                  $scope.showdropdown=true;
                  $scope.showlogin=false;
                  $scope.showenter=true;
                  $scope.disablelogin=true;
                  loginuser=res.user;
                  
                  //$rootScope.$broadcast('LoginSuccessful');
                 // $state.go('home');
                  
              } else {
         
                  $scope.message = 'Authetication Failed !';
                  alert("Authentication failed \n"+
                    "Please enter correct username password");
              }
            },
            error: function (error) { 
              $scope.message = 'Authetication Failed !';
              alert("failure whie loging in")
            }
            
          });
        };

        $scope.fetchUserDetails = function (username){
          
          $scope.loaderds = true;
          var user={};

          $.ajax({
                url: "api/users/"+username,
                error: function (e) {
                  //alert(JSON.stringify(e));
                },
                dataType: "json",
                contentType: 'application/json; charset=utf-8',
                type: "GET",
                async: false,
                cache: false,
                timeout: 30000,
                crossDomain: true,
                success: function (data) { 
                 
                 
                 user = data;
                // alert(JSON.stringify(data));
                }
               
            });
          return  user;
        };
        $scope.enterApp = function(){
        	
        	AuthService.user = loginuser;
        	AuthService.user.roleName=$scope.selectedvalues;
        	$rootScope.$broadcast('LoginSuccessful');
     
        	var routehtml="";
        	routehtml=$scope.selectedvalues;
        	routehtml=routehtml.trim();
          $state.go('home');
        	
        };
        $scope.getselectval = function (MyData) {
        
        	$scope.selectedvalues="";
        	$scope.selectedvalues= MyData.id;
        
        	};
    });
