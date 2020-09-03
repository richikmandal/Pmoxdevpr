angular.module('PmoxApp')
// Creating the Angular Controller
    .controller('OdBookController', function ($http, $scope, $state,AuthService,$rootScope) {
      
      $scope.user = AuthService.user;
      $scope.showEditOb=false;
      $scope.showAssociates=false;
      $scope.startDate='';
      $scope.endDate='';
      $scope.poDtlsList= [];
      $scope.poNumList= [];
      $scope.contractList= [];
      $scope.prjList= [];
      $scope.init = function () {

        $scope.showEditOb=false; 
        const sample = getMonths(new Date('2019-07-28'), new Date('2020-12-20'));
    
      };
      
      $scope.goHome=function(){
      
       $state.go('home');
       
      }
      
      $scope.addOrderBook=function(){
            
        $scope.showEditOb=true;
        
        $scope.loadOdMasterData($scope.user);
       
     }
      
      $scope.loadOdMasterData = function(userFrSearch){
        
        alert(JSON.stringify(userFrSearch))
        
        var usr = [];
        $.ajax({
          url: "api/ob/getPos",
          error: function (e) {
            alert('Invalid Result set for the request.'+JSON.stringify(e))
            //alert(JSON.stringify('error occured----'+e))
          },
          dataType: "json",
          contentType: 'application/json; charset=utf-8',
         // headers: {"Authorization": "Bearer "+AuthService.token},
          type: "POST",
          async: false,
          cache: false,
          data: JSON.stringify(userFrSearch),
          timeout: 30000,
          crossDomain: true,
          success: function (data) {               
           
            $scope.poDtlsList  = data;
          }
          
        });
        
        alert(JSON.stringify($scope.poDtlsList))
        
       $scope.poNumList = $scope.poDtlsList.map(item => item.poNum).filter((value, index, self) => self.indexOf(value) === index);
        alert(JSON.stringify($scope.poNumList))
        return $scope.poDtlsList;
        
      }
      
      $scope.getContrctNPidForPo=function(poNum){ 
        alert(23232323)
        
        $scope.contractList =  $scope.poDtlsList.filter(x => x.poNum === poNum);
        
        alert(JSON.stringify($scope.contractList));
       
     }
      
      $scope.goOrderBook=function(){   
        $scope.showEditOb=false;
        $state.go('odbook');
       
     }
      
      $scope.enableDateRange=function(){   
        
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
