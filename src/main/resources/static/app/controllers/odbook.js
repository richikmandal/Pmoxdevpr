angular.module('PmoxApp')
// Creating the Angular Controller
    .controller('OdBookController', function ($http, $scope, $state,AuthService,$rootScope) {
      
      $scope.user = AuthService.user;
      $scope.showEditOb=false;
      $scope.showAssociates=false;
      $scope.poDtlsList= [];
      $scope.poNumList= [];
      $scope.contractList= [];
      $scope.prjList= [];
      $scope.ibuHeadList= [];
      $scope.pgmList= [];
      $scope.pmList= [];
      $scope.customerName='';
      $scope.optyDesc='';
      $scope.contrctStatus='';
      $scope.contrctAmnt='';
      $scope.contrctStartDt='';
      $scope.contrctEndDt='';
      $scope.projectDesc='';
      $scope.projectType='';
      $scope.ibuHeadName='';
      $scope.pgmName='';
      $scope.pmName='';
      $scope.currentYear = '';
      $scope.prevYear = '';
      $scope.futureYear = '';
      
      
      $scope.init = function () {
        
        $scope.showEditOb=false; 
        $scope.currentYear = new Date().getFullYear();
        $scope.prevYear = parseInt($scope.currentYear)-1;
        $scope.futureYear = parseInt($scope.currentYear)+1;
        $scope.loadOdMasterData($scope.user);
        var projs = new Object();
        //projs.pid="allIbuHead";
        
       
        $scope.ibuHeadList.push($scope.poDtlsList.map(item => item.ibuHeadName).filter((value, index, self) => self.indexOf(value) === index));
  
       
        $scope.ibuHeadList.push('---All IBU Head---');
        $scope.selIbuHead = '---All IBU Head---';
        
        if($scope.ibuHeadList.length==1){
          $scope.selIbuHead = $scope.ibuHeadList[0][0];
          $scope.getDetailsDataPgm($scope.selIbuHead);
          
        }
    
      };
      
      $scope.goHome=function(){
      
       $state.go('home');
       
      }
      
      $scope.addOrderBook=function(){
            
        $scope.showEditOb=true;
        
        $scope.poNumList = $scope.poDtlsList.map(item => item.poNum).filter((value, index, self) => self.indexOf(value) === index);
       
     }
      
      $scope.loadOdMasterData = function(userFrSearch){
        
       // alert(JSON.stringify(userFrSearch))
        
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
        
       // alert(JSON.stringify($scope.poDtlsList))
        
      
       // alert(JSON.stringify($scope.poNumList))
       // return $scope.poDtlsList;
        
      }
      
        $scope.getDetailsDataPgm=function(ibuHeadId){ 
          
         // alert('ibuHeadId--'+ibuHeadId)
          
         // alert(JSON.stringify($scope.poDtlsList));
          
          var poTempList =  $scope.poDtlsList.filter(x => x.ibuHeadName == ibuHeadId);
         // alert(JSON.stringify(poTempList));
          
          $scope.pgmList = poTempList.map(item => item.pgmName).filter((value, index, self) => self.indexOf(value) === index);
         // alert('77777---'+JSON.stringify($scope.pgmList));
          $scope.pgmList.push('---All PGMs---');
          $scope.selPgm = '---All PGMs---';
          
          $scope.pmList = poTempList.map(item => item.pmName).filter((value, index, self) => self.indexOf(value) === index);
         // alert(JSON.stringify($scope.pgmList));
          $scope.pmList.push('---All PMs---');
          $scope.selPm = '---All PMs---';
          
          $scope.poNumList = poTempList.map(item => item.poNum).filter((value, index, self) => self.indexOf(value) === index);
         // alert(JSON.stringify($scope.poNumList));
                 
       }
      
      $scope.getContrctNPidForPo=function(poNum){ 
        
        $scope.clearAll();        
        $scope.contractList =  $scope.poDtlsList.filter(x => x.poNum === poNum);
               
     }
      
      $scope.getContrctDtl=function(contrctData){ 
       // alert('contrctNum--'+JSON.stringify(contrctData));
        var monthList = [];
        $scope.customerName=contrctData.customerName;
        $scope.optyDesc=contrctData.optyDesc;
        $scope.contrctStatus=contrctData.contrctStatus;
        $scope.contrctAmnt=contrctData.contrctAmnt;
        $scope.contrctStartDt=contrctData.contrctStartDt;
        $scope.contrctEndDt=contrctData.contrctEndDt;
        monthList = $scope.enableDateRange();
        var datestrt = new Date(contrctData.contrctStartDt);
        $scope.contrctStartDt = datestrt.toLocaleDateString()
        var dateend = new Date(contrctData.contrctEndDt);
        $scope.contrctEndDt = dateend.toLocaleDateString();
        
        angular.forEach(monthList, function (valueOut, keyOut) { 
          
          var id = valueOut.year+''+valueOut.month;
          
          document.getElementById(id).disabled = false;

        });
        
     }
      
      $scope.getProjectDtl=function(projData){ 
      
        $scope.projectDesc=projData.projectDesc;
        $scope.projectType=projData.projectType;
        $scope.ibuHeadName=projData.ibuHeadName;
        $scope.pgmName=projData.pgmName;
        $scope.pmName=projData.pmName;
               
     }
      
      $scope.goOrderBook=function(){   
        $scope.showEditOb=false;
        $state.go('odbook');
       
     }
      
      $scope.clearAll=function(){   
        
        $scope.customerName='';
        $scope.optyDesc='';
        $scope.contrctStatus='';
        $scope.contrctAmnt='';
        $scope.contrctStartDt='';
        $scope.contrctEndDt='';
        $scope.projectDesc='';
        $scope.projectType='';
        $scope.ibuHeadName='';
        $scope.pgmName='';
        $scope.pmName='';
       
     }
      
      $scope.enableDateRange=function(){   
        
        const sample = getMonths(new Date($scope.contrctStartDt), new Date($scope.contrctEndDt));
        return sample;
     }
      
        const getMonths = (fromDate, toDate) => {
          const fromYear =  fromDate.getFullYear();
          const fromMonth = fromDate.getMonth();
          const toYear =    toDate.getFullYear();
          const toMonth =   toDate.getMonth();
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
