angular.module('PmoxApp')
// Creating the Angular Controller
    .controller('OdBookController', function ($http, $scope, $state,AuthService,$rootScope) {
      
      $scope.user = AuthService.user;
      $scope.showEditOb=false;
      $scope.showAssociates=false;
      $scope.startDate='';
      $scope.endDate='';
      $scope.init = function () {

        $scope.showEditOb=false; 
        const sample = getMonths(new Date('2019-07-28'), new Date('2020-12-20'));
    
      };
      
      $scope.goHome=function(){
      
       $state.go('home');
       
      }
      
      $scope.addOrderBook=function(){
            
        $scope.showEditOb=true;
       
     }
      
      $scope.goOrderBook=function(){   
        $scope.showEditOb=false;
        $state.go('odbook');
       
     }
      
      $scope.enableDateRange=function(){   
        
        alert(111111);
        alert('$scope.startDate--'+$scope.startDate);
        alert('$scope.endDate----'+$scope.endDate);
        const sample = getMonths(new Date($scope.startDate), new Date($scope.endDate));
        alert(JSON.stringify(sample));
        const sample1 = getMonths(new Date('2019-07-28'), new Date('2020-12-20'));
        alert(JSON.stringify(sample1));
       
     }
      
        const getMonths = (fromDate, toDate) => {
          const fromYear = fromDate.getFullYear();
          const fromMonth = fromDate.getMonth();
          const toYear = toDate.getFullYear();
          const toMonth = toDate.getMonth();
          const months = [];
          for(let year = fromYear; year <= toYear; year++) {
            let month = year === fromYear ? fromMonth : 0;
            const monthLimit = year === toYear ? toMonth : 11;
            for(; month <= monthLimit; month++) {
              months.push({ year, month })
            }
          }
          return months;
        }
     
        
    });
