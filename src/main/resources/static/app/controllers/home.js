angular.module('PmoxApp')
// Creating the Angular Controller
    .controller('HomeController', function ($http, $scope, $state,AuthService,$rootScope) {
        $scope.user = AuthService.user;
      // $scope.user = AuthService.user.name;
        // $scope.userPrjData = '';
          var selectedPmId='';
          var selectedPrjId='';
          $scope.showProjects=false;
          $scope.showAssociates=false;
          $scope.showPnL=false;
          $scope.showOB=true;
          $scope.makeShrink = false; 
          $scope.sbuName = '';
          $scope.ibgName = '';
          $scope.ibuName = '';
          $scope.sbuNames =[];
          $scope.ibgNames =[];
          $scope.ibuNames=[];
          $scope.pgManagers=[];
          $scope.sPgManagers=[];
          $scope.manNames=[];
          
          
          $scope.prjTech = [];
          $scope.prjTechCnt = [];
          $scope.prjType = [];
          $scope.prjTypeCnt = [];
          $scope.pnlSummaryData = [];
          $scope.revProjectionData = [];
          $scope.init = function () {
           $scope.disableTabs=true;           
           $scope.user = $scope.loadProjectMasterData($scope.user)[0] ;
           
           alert(JSON.stringify($scope.user.projMasterData))
           
           $scope.totalProjectCount = $scope.user.totalProjectCount;
           $scope.totalOffShoreCount = $scope.user.totalOffShoreCount;
           $scope.totalOnShoreCount = $scope.user.totalOnShoreCount;
           $scope.totalRevenue = $scope.user.totalRevenue;
           $scope.totalEbidta = $scope.user.totalEbidta;
          // $scope.loadPricingMdlData($scope.user.projMasterData);
           
           $scope.loadFiltersWithStatusData(selectedPmId,selectedPrjId,$scope.user.projMasterData);
           $scope.loadProjStatus($scope.user.projMasterData);
           $scope.loadProjManaged($scope.user.projMasterData); 
           $scope.loadProjTech($scope.user.projMasterData);
           $scope.loadProjType($scope.user.projMasterData);
           $scope.loadProjLocChrt($scope.user.resourceMap);
          // $scope.loadProjRevEbidtaChrt($scope.user);
           $scope.loadProjRevProjectionData($scope.user);

              
         };
         
         $scope.loadProjectMasterData = function(userFrSearch){
          
           var usr = [];
           $.ajax({
             url: "api/projects/getprojectsfruser",
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
              
               usr.push(data);
             }
             
           });
           
           return usr;
           
         }
         
         $scope.loadPricingMdlData = function(projMasterData){
           
           $scope.totalFp=0;
           $scope.totalTnM=0;
           $scope.totalOther=0;
           
           var uniqsPricing = projMasterData.reduce((acc, val) => {
             acc[val.pricingModel] = acc[val.pricingModel] === undefined ? 1 : acc[val.pricingModel] += 1;
             return acc;
           }, {});
                     
           var fpVal = 0;
           var tnmVal = 0;
           var otherVal = 0
           
           Object.keys(uniqsPricing).forEach(function(key) {
 
             if(key!='FP' && key!='T&M'){
               otherVal = otherVal+parseInt(uniqsPricing[key])
             }
             else if(key==='FP')
             {
               $scope.totalFp = uniqsPricing[key];
             }
             else{
               $scope.totalTnM = uniqsPricing[key];
             }
         });
           
           $scope.totalOther = otherVal;
         }
         
         $scope.loadPmSeriesData = function(userFrSearch){
           
           var usr = [];
           $.ajax({
             url: "api/projects/getPmSeriesData",
             error: function (e) {
               //alert('Invalid Result set.......'+JSON.stringify(e))
               
                $("#alertMsg").html("The required data is not available for the selected user.");                          
                $('#alertModal').modal("show");
            
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
              //alert(JSON.stringify(data))
               usr.push(data);
             }
             
           });
           
           return usr;
           
         }
         
         $scope.loadPnLSummaryData = function(userFrSearch){
           
           $.ajax({
             url: "api/projects/getPnLSummary",
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
              //alert(JSON.stringify(data))
               $scope.pnlSummaryData = data;
             }
             
           });
           
         }
         

         $scope.loadFiltersWithStatusData = function(selectedPmId,selectedPrjId,projMasterData){
           
           var lastPgmId = 0;
           var lastPgmName = '';
           var lastPmId = 0;
           var lastPmName = '';
                      
           if(selectedPmId==='' && selectedPrjId==='' )
           {
             
             $scope.getDistinctSbu(projMasterData);
             $scope.getDistinctIbg(projMasterData);
             $scope.getDistinctIbu(projMasterData);
             $scope.getDistinctSales(projMasterData);
             $scope.getDistinctPGM(projMasterData);
             $scope.getDistinctPM(projMasterData);
             $scope.getDistinctProject(projMasterData);
           
           }
                      
         }
         
         
         $scope.getDistinctSbu=function(projMasterData){
           
           var sbu = new Object();
           sbu.id="allsbu";
           sbu.name="---All SBU---";
           $scope.sbuNames.push(sbu);
           
           var distinctSbu = [...new Set(projMasterData.map(sbu => sbu.sbu+':'+sbu.sbu))];
           
           if(distinctSbu.length==1){
             var ibuArr = distinctSbu[0].split(':');
             var ibu = new Object();
             ibu.id=ibuArr[0];
             ibu.name=ibuArr[1];
             $scope.sbuNames = [];
             $scope.sbuNames.push(ibu);
             $scope.sbuName=ibu;
             
           }else{
             
               angular.forEach(distinctSbu, function (valueOut, keyOut) {
                 
                 var ibuArr = valueOut.split(':');
                 var ibu = new Object();
                 ibu.id=ibuArr[0];
                 ibu.name=ibuArr[1];
                 $scope.sbuNames.push(ibu);
                // alert($scope.ibuNames.length)
               
             });  
           }
         
         }; 
         
         $scope.getDistinctIbg=function(projMasterData){
           
           var ibg = new Object();
           ibg.id="allibg";
           ibg.name="---All IBG---";
           $scope.ibgNames.push(ibg);
           
          var distinctIbg = [...new Set(projMasterData.map(ibg => ibg.ibg+':'+ibg.ibg))];
           
           if(distinctIbg.length==1){
             var ibuArr = distinctIbg[0].split(':');
             var ibu = new Object();
             ibu.id=ibuArr[0];
             ibu.name=ibuArr[1];
             $scope.ibgNames = [];
             $scope.ibgNames.push(ibu);
             $scope.ibgName=ibu;
             
           }else{
             
               angular.forEach(distinctIbg, function (valueOut, keyOut) {
                 
                 var ibuArr = valueOut.split(':');
                 var ibu = new Object();
                 ibu.id=ibuArr[0];
                 ibu.name=ibuArr[1];
                 $scope.ibgNames.push(ibu);
                // alert($scope.ibuNames.length)
               
             });  
           }
         
         }; 
         
         
         
         $scope.getDistinctIbu=function(projMasterData){
           
           var ibu = new Object();
           ibu.id="allibu";
           ibu.name="---All IBU---";
           $scope.ibuNames.push(ibu);
                      
           var distinctIbu = [...new Set(projMasterData.map(ibu => ibu.ibu+':'+ibu.ibu))];
           
           if(distinctIbu.length==1){
             var ibuArr = distinctIbu[0].split(':');
             var ibu = new Object();
             ibu.id=ibuArr[0];
             ibu.name=ibuArr[1];
             $scope.ibuNames = [];
             $scope.ibuNames.push(ibu);
             $scope.ibuName=ibu;
             
           }else{
             
               angular.forEach(distinctIbu, function (valueOut, keyOut) {
                 
                 var ibuArr = valueOut.split(':');
                 var ibu = new Object();
                 ibu.id=ibuArr[0];
                 ibu.name=ibuArr[1];
                 $scope.ibuNames.push(ibu);
                // alert($scope.ibuNames.length)
               
             });  
           }
         
         }; 
         
         $scope.getDistinctSales=function(projMasterData){
           
           var sPgm = new Object();
           sPgm.id="allspgm";
           sPgm.name="---All Sales Mangers---";
           $scope.sPgManagers.push(sPgm);
           
           var distinctsPgm = [...new Set(projMasterData.map(spgm => spgm.sPgmId+':'+spgm.sPgmName))];
           
           // alert(JSON.stringify(distinctsPgm))
            
            angular.forEach(distinctsPgm, function (valueOut, keyOut) {
              
                var pgmArr = valueOut.split(':');
                var pgm = new Object();
                pgm.id=pgmArr[0];
                pgm.name=pgmArr[1];
                $scope.sPgManagers.push(pgm);
                $scope.sPgmdata=$scope.sPgManagers[0];
              
            });  
         
         }; 
         
         $scope.getDistinctPGM=function(projMasterData){
           
           var pgm = new Object();
           pgm.id="allpgm";
           pgm.name="---All PGMs---";
           $scope.pgManagers.push(pgm);
           
           var distinctPgm = [...new Set(projMasterData.map(pgm => pgm.pgmId+':'+pgm.pgmName))];
           
           angular.forEach(distinctPgm, function (valueOut, keyOut) {
             
               var pgmArr = valueOut.split(':');
               var pgm = new Object();
               pgm.id=pgmArr[0];
               pgm.name=pgmArr[1];
               $scope.pgManagers.push(pgm);
               $scope.pgmdata=$scope.pgManagers[0];
             
           });  
           
         
         }; 
         
         $scope.getDistinctPM=function(projMasterData){
           
           var managers = new Object();
           managers.pid="allpm";
           managers.pmname="---All PMs---";
           $scope.manNames.push(managers);
           $scope.pmData=$scope.manNames[0];
           
           var distinctPm = [...new Set(projMasterData.map(pm => pm.pmId+':'+pm.pmName))];
           
           angular.forEach(distinctPm, function (valueOut, keyOut) {
             
               var pmArr = valueOut.split(':');
               var managers = new Object();
               managers.pid=pmArr[0];
               managers.pmname=pmArr[1];
               $scope.manNames.push(managers);
             
             
           });  
         
         }; 
         
         $scope.getDistinctProject=function(projMasterData){
           $scope.projects=[];
           var projects = new Object();
           projects.pid="allprj";
           projects.pname="---All Projects---";
           $scope.projects.push(projects);
           $scope.selProject=$scope.projects[0];
           
           var distinctProject = [...new Set(projMasterData.map(project => project.projectId+':'+project.projectDescription))];
           
           angular.forEach(distinctProject, function (valueOut, keyOut) {
             
               var projectArr = valueOut.split(':');
               var projects = new Object();
               projects.pid=projectArr[0];
               projects.pname=projectArr[1];
               $scope.projects.push(projects);
             
             
           });  
         
         }; 
         
         
         $scope.getFilterData=function(filtrObj,roleFilter){
           
           alert('filtrObj--'+JSON.stringify(filtrObj));
           alert('roleFilter--'+roleFilter);
           
             if(roleFilter==='SBU'){
               
             }else if(roleFilter==='IBG'){
               
             }else if(roleFilter==='IBU'){
               
             }else if(roleFilter==='SALESM'){
               
             }else if(roleFilter==='PGM'){
               
               
               
             }else if(roleFilter==='PM'){
               
             }else if(roleFilter==='PM'){
               
             }
           
           
           }; 
         
         $scope.getDetailsDataSPgm=function(spgm){
           
           //  alert('inside the getDetailsDatapgm '+JSON.stringify(pgm))
             
             $scope.projects=[];
             $scope.manNames=[];
             $scope.pmData={};
             var lastPmId = '';
             var userDataPrj =[];
             $scope.showdropdown=true; 
             
              
              if(pgm.id==='allpgm'){
                userDataPrj = $scope.user;
                var user = new Object();
                user.username = $scope.user.username;
                user.name = $scope.user.name;
                user.roleName = $scope.user.roleName;
               // alert('allpgm--'+JSON.stringify(user))
                $scope.loadProjRevEbidtaChrt(user);
              }else{
                var user = new Object();
                user.username = pgm.id;
                user.name = pgm.name;
                user.roleName = 'PGM';
                userDataPrj = $scope.loadProjectMasterData(user)[0];
                $scope.loadProjRevEbidtaChrt(user);
              }
   
              $scope.totalProjectCount = userDataPrj.totalProjectCount;
              $scope.totalOffShoreCount = userDataPrj.totalOffShoreCount;
              $scope.totalOnShoreCount = userDataPrj.totalOnShoreCount;
              $scope.totalRevenue = userDataPrj.totalRevenue;
              $scope.totalEbidta = userDataPrj.totalEbidta;
              $scope.loadPricingMdlData(userDataPrj.projMasterData);
              
              $scope.loadProjStatus(userDataPrj.projMasterData);  
              $scope.loadProjManaged(userDataPrj.projMasterData);
              $scope.loadProjTech(userDataPrj.projMasterData);
              $scope.loadProjType(userDataPrj.projMasterData);
              $scope.loadProjLocChrt(userDataPrj.resourceMap);
              
              var managers = new Object();
              managers.pid="allpm";
              managers.pmname="---All PMs---";
              $scope.manNames.push(managers);
              $scope.pmData=$scope.manNames[0];
             // alert(JSON.stringify($scope.pmData)+'$scope.pmData----'+JSON.stringify($scope.manNames))
             // $scope.pmData=$scope.manNames[0];
             // alert(JSON.stringify($scope.pmData)+'$scope.pmData----'+JSON.stringify($scope.manNames))
              
              var projs = new Object();
              projs.pid="allprj";
              projs.pname="---All Projects---";
              $scope.projects.push(projs);
             
              angular.forEach(userDataPrj.projMasterData, function (valueOut, keyOut) { 
                         
                    if(valueOut.pmId!=lastPmId)
                    {
                      var managers = new Object();
                      managers.pid=valueOut.pmId;
                      managers.pmname=valueOut.pmName;
                      $scope.manNames.push(managers);
      
                      lastPmId = valueOut.pmId;
                      lastPmName = valueOut.pmName;
                    } 
                    
                    var projects = new Object();
                    projects.pid=valueOut.projectId;
                    projects.pname=valueOut.projectDescription;
                    $scope.projects.push(projects);
              }); 
              
              //alert(JSON.stringify($scope.pmData)+'$scope.pmData----'+JSON.stringify($scope.manNames))
              
              $scope.pmData=$scope.manNames[0];
              $scope.selProject=$scope.projects[0];
              
           }; 
         
         $scope.getDetailsDataPgm=function(pgm){
           
         //  alert('inside the getDetailsDatapgm '+JSON.stringify(pgm))
           
           $scope.projects=[];
           $scope.manNames=[];
           $scope.pmData={};
           var lastPmId = '';
           var userDataPrj =[];
           $scope.showdropdown=true; 
           
            
            if(pgm.id==='allpgm'){
              userDataPrj = $scope.user;
              var user = new Object();
              user.username = $scope.user.username;
              user.name = $scope.user.name;
              user.roleName = $scope.user.roleName;
             // alert('allpgm--'+JSON.stringify(user))
              $scope.loadProjRevEbidtaChrt(user);
            }else{
              var user = new Object();
              user.username = pgm.id;
              user.name = pgm.name;
              user.roleName = 'PGM';
              userDataPrj = $scope.loadProjectMasterData(user)[0];
              $scope.loadProjRevEbidtaChrt(user);
            }
 
            $scope.totalProjectCount = userDataPrj.totalProjectCount;
            $scope.totalOffShoreCount = userDataPrj.totalOffShoreCount;
            $scope.totalOnShoreCount = userDataPrj.totalOnShoreCount;
            $scope.totalRevenue = userDataPrj.totalRevenue;
            $scope.totalEbidta = userDataPrj.totalEbidta;
            $scope.loadPricingMdlData(userDataPrj.projMasterData);
            
            $scope.loadProjStatus(userDataPrj.projMasterData);  
            $scope.loadProjManaged(userDataPrj.projMasterData);
            $scope.loadProjTech(userDataPrj.projMasterData);
            $scope.loadProjType(userDataPrj.projMasterData);
            $scope.loadProjLocChrt(userDataPrj.resourceMap);
            
            var managers = new Object();
            managers.pid="allpm";
            managers.pmname="---All PMs---";
            $scope.manNames.push(managers);
            $scope.pmData=$scope.manNames[0];
           // alert(JSON.stringify($scope.pmData)+'$scope.pmData----'+JSON.stringify($scope.manNames))
           // $scope.pmData=$scope.manNames[0];
           // alert(JSON.stringify($scope.pmData)+'$scope.pmData----'+JSON.stringify($scope.manNames))
            
            var projs = new Object();
            projs.pid="allprj";
            projs.pname="---All Projects---";
            $scope.projects.push(projs);
           
            angular.forEach(userDataPrj.projMasterData, function (valueOut, keyOut) { 
                       
                  if(valueOut.pmId!=lastPmId)
                  {
                    var managers = new Object();
                    managers.pid=valueOut.pmId;
                    managers.pmname=valueOut.pmName;
                    $scope.manNames.push(managers);
    
                    lastPmId = valueOut.pmId;
                    lastPmName = valueOut.pmName;
                  } 
                  
                  var projects = new Object();
                  projects.pid=valueOut.projectId;
                  projects.pname=valueOut.projectDescription;
                  $scope.projects.push(projects);
            }); 
            
            //alert(JSON.stringify($scope.pmData)+'$scope.pmData----'+JSON.stringify($scope.manNames))
            
            $scope.pmData=$scope.manNames[0];
            $scope.selProject=$scope.projects[0];
            
         }; 
         
         $scope.getDetailsDataPm=function(pm){
                     
           $scope.projects=[];
           var userDataPrj =[];
           $scope.showdropdown=true; 
           var projs = new Object();
           projs.pid="allprj";
           projs.pname="---All Projects---";
            $scope.projects.push(projs);
            $scope.selProject=$scope.projects[0];
            //alert('$scope.pgmdata.id--'+$scope.pgmdata.id)
            if(pm.pid==='allpm'){
              //alert('pm.pid--'+pm.pid)
              var user = new Object();
              user.username =  $scope.pgmdata.id;
              user.name = $scope.pgmdata.name;
              user.roleName = 'PGM';
              if($scope.pgmdata.id==='allpgm')
                {
                  userDataPrj = $scope.user;
                  $scope.loadProjRevEbidtaChrt($scope.user);
                }
              else{
                 userDataPrj = $scope.loadProjectMasterData(user)[0];
                 $scope.loadProjRevEbidtaChrt(user);
              }
             
            }else{
              
              var user = new Object();
              user.username = pm.pid;
              user.name = pm.pmname;
              user.roleName = 'PM';
              userDataPrj = $scope.loadProjectMasterData(user)[0];
              
              $scope.loadProjRevEbidtaChrt(user);
            }
            
            $scope.totalProjectCount = userDataPrj.totalProjectCount;
            $scope.totalOffShoreCount = userDataPrj.totalOffShoreCount;
            $scope.totalOnShoreCount = userDataPrj.totalOnShoreCount;
            $scope.totalRevenue = userDataPrj.totalRevenue;
            $scope.totalEbidta = userDataPrj.totalEbidta;
            $scope.loadPricingMdlData(userDataPrj.projMasterData);  
            $scope.loadFiltersWithStatusData(pm.pid,'',userDataPrj.projMasterData);
            $scope.loadProjStatus(userDataPrj.projMasterData);  
            $scope.loadProjManaged(userDataPrj.projMasterData);
            $scope.loadProjTech(userDataPrj.projMasterData);
            $scope.loadProjType(userDataPrj.projMasterData);
            $scope.loadProjLocChrt(userDataPrj.resourceMap);
            
            angular.forEach(userDataPrj.projMasterData, function (valueOut, keyOut) { 
                       
                    if(pm.pid==='allpm') {
                      
                        var managers = new Object();
                        managers.pid=valueOut.projectId;
                        managers.pname=valueOut.projectDescription;
                        $scope.projects.push(managers);
                      
                    }else{
                      
                        if(valueOut.pmId==pm.pid){
                          
                          var managers = new Object();
                          managers.pid=valueOut.projectId;
                          managers.pname=valueOut.projectDescription;
                          $scope.projects.push(managers);
  
                      } 
                      
                    }
            }); 
            
         }; 
         
         $scope.getDetailsDataPrj=function(selProject){
              
           if(selProject.pid==='allprj'){
           
             if($scope.pmData.pid==='allpm'){
             
               var user = new Object();
               user.username =  $scope.pgmdata.id;
               user.name = $scope.pgmdata.name;
               user.roleName = 'PGM';
               user.projectSelected = '';
               if($scope.pgmdata.id==='allpgm')
                 {
                   userDataPrj = $scope.user;
                   $scope.loadProjRevEbidtaChrt($scope.user);
                 }
               else{
                  userDataPrj = $scope.loadProjectMasterData(user)[0];
                  $scope.loadProjRevEbidtaChrt(user);
                 // $scope.loadProjRevEbidtaChrt(user);
               }
              
             }else{
               
               var user = new Object();
               user.username = $scope.pmData.pid;
               user.name = $scope.pmData.pmname;
               user.roleName = 'PM';
               user.projectSelected = '';
               userDataPrj = $scope.loadProjectMasterData(user)[0];
               
               $scope.loadProjRevEbidtaChrt(user);
             }
             
           }
           else{
             var user = new Object();
             user.username = $scope.pmData.pid;
             user.name = $scope.pmData.pmname;
             user.roleName = 'PM';
             user.projectSelected = selProject.pid;
             $scope.loadProjRevEbidtaChrt(user);
             userDataPrj = $scope.loadProjectMasterData(user)[0];
           }        

          $scope.totalProjectCount = userDataPrj.totalProjectCount;
          $scope.totalOffShoreCount = userDataPrj.totalOffShoreCount;
          $scope.totalOnShoreCount = userDataPrj.totalOnShoreCount;
          $scope.totalRevenue = userDataPrj.totalRevenue;
          $scope.totalEbidta = userDataPrj.totalEbidta;
          $scope.loadPricingMdlData(userDataPrj.projMasterData);
          $scope.loadFiltersWithStatusData('',selProject.pid,userDataPrj.projMasterData);         
          $scope.loadProjStatus(userDataPrj.projMasterData);         
          $scope.loadProjManaged(userDataPrj.projMasterData);          
          $scope.loadProjTech(userDataPrj.projMasterData);         
          $scope.loadProjType(userDataPrj.projMasterData);          
          $scope.loadProjLocChrt(userDataPrj.resourceMap);

         }; 
         
         $scope.loadProjStatus = function(projMasterData) {
           
           var uniqsStatus = projMasterData.reduce((acc, val) => {
             acc[val.status] = acc[val.status] === undefined ? 1 : acc[val.status] += 1;
             return acc;
           }, {});
           
          var statusFlag = [];
          var statusFlagData = [];
          var statusSeriesData = [];
         
            angular.forEach(uniqsStatus, function (value, key) {
              
              statusFlag =  [key, value];
              statusFlagData.push(statusFlag);
            
            });
            
            statusSeriesData.push({data:statusFlagData});

            $scope.chartOptionStat = {
                    chart: {
                      type: 'pie',
                      options3d: {
                        enabled: true,
                        alpha: 45
                    }
                  },
                  title: {
                      text: 'Project Status',
                  },
                 credits: {
                    enabled: false
                  },
                  plotOptions: {
                        pie: {
                          dataLabels: {
                            enabled: true,
                            format: '<b>{point.name}</b>: {point.y}',
                          },
                          innerSize: 100,
                          depth: 45
                      }
                  },
                  colors: ['#79d279','#ffb84d','#00bfff'],
                  series: statusSeriesData
                };
         }
         
         $scope.loadProjManaged = function(projMasterData) {
                      
           var uniqsStatus = projMasterData.reduce((acc, val) => {
             acc[val.deliveryOwnership] = acc[val.deliveryOwnership] === undefined ? 1 : acc[val.deliveryOwnership] += 1;
             return acc;
           }, {});
           
          var statusFlag = [];
          var statusFlagData = [];
          var managedSeriesData = [];
         
            angular.forEach(uniqsStatus, function (value, key) {
  
              statusFlag =  [key, value];
              statusFlagData.push(statusFlag);
            
            });
            
            managedSeriesData.push({data:statusFlagData});
     
            $scope.chartOptionsMan = {
                    chart: {
                      type: 'pie',
                      options3d: {
                        enabled: true,
                        alpha: 45
                    }
                  },
                  title: {
                      text: 'Project Managed',
                  },
                 credits: {
                    enabled: false
                  },
                  plotOptions: {
                        pie: {
                          dataLabels: {
                            enabled: true,
                            format: '<b>{point.name}</b>: {point.y}',
                          },
                          innerSize: 100,
                          depth: 45
                      }
                  },
                  colors: ['#b3e0ff','#bf80ff','#e6e600','#ff8c66'],
                  series: managedSeriesData
                };
         }
         
         $scope.loadProjTech = function(projMasterData) {
           
           var arr = projMasterData;
           var pieDataP = [];
           
           var uniqs = arr.reduce((acc, val) => {
             acc[val.projectTechnology] = acc[val.projectTechnology] === undefined ? 1 : acc[val.projectTechnology] += 1;
             return acc;
           }, {});
           
           angular.forEach(uniqs, function (value, key) {
             
             var pieData = new Object();
             pieData.name = key;
             pieData.y =value;
             pieDataP.push(pieData);
           
           });

             $scope.pieData = pieDataP ; 
         }
         
         $scope.loadProjType = function(projMasterData) {

           var arr = projMasterData;
           var pieDataPtyp = [];
                    
           var uniqs = arr.reduce((acc, val) => {
             acc[val.projectType] = acc[val.projectType] === undefined ? 1 : acc[val.projectType] += 1;
             return acc;
           }, {});
           
           angular.forEach(uniqs, function (value, key) {
             
             var pieData = new Object();
             pieData.name = key;
             pieData.y =value;
             pieDataPtyp.push(pieData);
           
           });

             $scope.pieFrPtch = pieDataPtyp ;
         }
         
         $scope.loadProjLocChrt = function(resourceMap) {

           var pieDataPtyp = [];
           var pieDataPtypBand = [];
           var resMapData = [];
           $scope.seriesHtrg = [];
           $scope.seriesOnOff =[];

           angular.forEach(resourceMap, function (valueOut, keyOut) {

             angular.forEach(valueOut, function (valueIn, keyIn) {
             
                 var pieData = new Object();
                 pieData.country = valueIn.country;
                 pieData.band= valueIn.band;
                 pieData.htrFlag= valueIn.htrFlag;
                 pieData.onOff= valueIn.onOff;
                 resMapData.push(pieData);
             });
           });
           
           var uniqsHtr = resMapData.reduce((acc, val) => {
             acc[val.htrFlag] = acc[val.htrFlag] === undefined ? 1 : acc[val.htrFlag] += 1;
             return acc;
           }, {});
           
                 
           var reversedArr = $scope.reverseByValue(uniqsHtr);
         
            $scope.seriesHtrg.push({data:reversedArr});
     
            $scope.chartOptionsHtr = {
                  chart: {
                    type: 'pyramid',
                    spacingBottom: 10,
                    spacingTop: 10,
                    spacingLeft: 10,
                    spacingRight: 10,
                    style: {
                      fontFamily: 'Bahnschrift'
                    }
                    
                },
                title: {
                    text: 'HTR Distribution'
                },
                credits: {
                  enabled: false
                },
                plotOptions: {
                    series: {
                      dataLabels: {
                          enabled: true,
                          format: '<b>{point.name}</b> ({point.y:,.0f})',
                          softConnector: true
                      }
                      
                  },
                  center: ['40%', '50%'],
                  width: '80%'
                },
                // colors: ['#006600','#800000','#3285a8'],
                series: $scope.seriesHtrg,
                responsive: {
                  rules: [{
                      condition: {
                          maxWidth: 500
                      },
                      chartOptions: {
                          plotOptions: {
                              series: {
                                  dataLabels: {
                                      inside: true
                                  },
                                  center: ['50%', '50%'],
                                  width: '100%'
                              }
                          }
                      }
                  }]
              }
              };
          
         // alert('resMapData--'+JSON.stringify(resMapData))
          
          var uniqsOnOff = resMapData.reduce((acc, val) => {
            acc[val.onOff] = acc[val.onOff] === undefined ? 1 : acc[val.onOff] += 1;
            return acc;
          }, {});
          
          //alert('uniqsOnOff--'+JSON.stringify(uniqsOnOff))
          
          var onOffFlag = [];
          var onOffFlagData = [];
         
            angular.forEach(uniqsOnOff, function (value, key) {
              
              onOffFlag =  [key, value];
              onOffFlagData.push(onOffFlag);
            
            });
            
           $scope.seriesOnOff.push({data:onOffFlagData});

           $scope.chartOptionsOnOff = {
                  chart: {
                    type: 'pie',
                    options3d: {
                      enabled: true,
                      alpha: 45
                  },
                 style: {
                   fontFamily: 'Bahnschrift'
                 }
                },
                title: {
                    text: 'On/Off Distribution'
                },
                credits: {
                  enabled: false
                },
                plotOptions: {
                    series: {
                      dataLabels: {
                          enabled: true,
                          format: '<b>{point.name}</b> ({point.percentage:,.0f}%)',
                          softConnector: true
                      },
                      innerSize: 100,
                      depth: 45
                  }
                },
                 colors: ['#99CC00','#CC6666'],
                series: $scope.seriesOnOff
              };
           
         //  alert(resMapData+'---resMapData--'+JSON.stringify(resMapData))
           
           var uniqsCntry = resMapData.reduce((acc, val) => {
             acc[val.country] = acc[val.country] === undefined ? 1 : acc[val.country] += 1;
             return acc;
           }, {});
           
           //alert(uniqsCntry+'-----uniqs-cntry---'+JSON.stringify(uniqsCntry))
           
           angular.forEach(uniqsCntry, function (value, key) {
             
             var pieData = new Object();
             pieData.name = key;
             pieData.y =value;
             pieDataPtyp.push(pieData);
           
           });
           
           $scope.pieFrCntry = pieDataPtyp ;
             
           var uniqsBand = resMapData.reduce((acc, val) => {
             acc[val.band] = acc[val.band] === undefined ? 1 : acc[val.band] += 1;
             return acc;
           }, {});
           
          // alert(uniqsCntry+'-----uniqs-cntry---'+JSON.stringify(uniqsCntry))
           
           angular.forEach(uniqsBand, function (value, key) {
             
             var pieData = new Object();
             pieData.name = key;
             pieData.y =value;
             pieDataPtypBand.push(pieData);
           
           });
           
           $scope.pieFrBand = pieDataPtypBand ;
                         
         }
         
         
         $scope.loadProjRevProjectionData= function(user) {
           
                    
           var revProjectionData = [];
           var foreCastSeriesData = [];
           var upsideSeriesData = [];
           var pipeSeriesData = [];
           var targetSeriesData = [];
           
           $.ajax({
             url: "api/projects/getRevenueProjData",
             error: function (e) {
               //alert('Invalid Result set.......'+JSON.stringify(e))
               
                $("#alertMsg").html("The required data is not available for the selected user.");                          
                $('#alertModal').modal("show");
            
              //alert(JSON.stringify('error occured----'+e))
             },
             dataType: "json",
             contentType: 'application/json; charset=utf-8',
            // headers: {"Authorization": "Bearer "+AuthService.token},
             type: "POST",
             async: false,
             cache: false,
             data: JSON.stringify(user),
             timeout: 30000,
             crossDomain: true,
             success: function (data) {               
              //alert(JSON.stringify(data))
               alert(data[0].category);
               console.log(JSON.stringify(data));
                              
               revProjectionData = data;
               var foreCast = new Object();
               foreCast.cfyQOne = parseFloat((data[0].cfyQOne + data[1].cfyQOne + data[2].cfyQOne + data[3].cfyQOne).toFixed(3));
               foreCast.cfyQTwo =  parseFloat((data[0].cfyQTwo + data[1].cfyQTwo + data[2].cfyQTwo + data[3].cfyQTwo).toFixed(3));
               foreCast.cfyQThree =  parseFloat((data[0].cfyQThree + data[1].cfyQThree + data[2].cfyQThree + data[3].cfyQThree).toFixed(3));
               foreCast.cfyQFour =  parseFloat((data[0].cfyQFour + data[1].cfyQFour + data[2].cfyQFour + data[3].cfyQFour).toFixed(3)); 
               foreCast.category = "f6-ForeCast";
               foreCastSeriesData= [foreCast.cfyQOne,foreCast.cfyQTwo,foreCast.cfyQThree,foreCast.cfyQFour];
               //foreCastSeriesData.push(foreCast);
               revProjectionData.push(foreCast);
               
               
               var gap = new Object();
               gap.cfyQOne = parseFloat((8.75 - foreCast.cfyQOne).toFixed(3)); 
               gap.cfyQTwo = parseFloat((8.75 - foreCast.cfyQTwo).toFixed(3)) ;
               gap.cfyQThree = parseFloat((8.75 - foreCast.cfyQThree).toFixed(3));
               gap.cfyQFour = parseFloat((8.75 - foreCast.cfyQFour).toFixed(3)) ;
               gap.category = "g7-Gap";
               revProjectionData.push(gap);
              
               
               var target = new Object();
               target.cfyQOne = 8.75; 
               target.cfyQTwo =  8.75; 
               target.cfyQThree =  8.75; 
               target.cfyQFour =  8.75; 
               target.category = "a1-Target";
               targetSeriesData= [target.cfyQOne,target.cfyQTwo,target.cfyQThree,target.cfyQFour];
               revProjectionData.push(target);
               
               var upside = new Object();
               upside.cfyQOne = data[4].cfyQOne; 
               upside.cfyQTwo =  data[4].cfyQTwo;
               upside.cfyQThree = data[4].cfyQThree;
               upside.cfyQFour =  data[4].cfyQFour;
               upside.category = "j10-Upside";
               upsideSeriesData = [data[4].cfyQOne,data[4].cfyQTwo,data[4].cfyQThree,data[4].cfyQFour];
               //revProjectionData.push(target);
               
               var pipe = new Object();
               pipe.cfyQOne = data[5].cfyQOne; 
               pipe.cfyQTwo =  data[5].cfyQTwo;
               pipe.cfyQThree = data[5].cfyQThree;
               pipe.cfyQFour =  data[5].cfyQFour;
               pipe.category = "j10-Pipeline(P0 to P2)";
               pipeSeriesData = [data[5].cfyQOne,data[5].cfyQTwo,data[5].cfyQThree,data[5].cfyQFour];
              // pipeSeriesData.push(upside);
               
               
               var lstForcst = new Object();
               lstForcst.cfyQOne = 0.00; 
               lstForcst.cfyQTwo =  0.00; 
               lstForcst.cfyQThree =  0.00; 
               lstForcst.cfyQFour =  0.00; 
               lstForcst.category = "h8-Last Forecast";
               revProjectionData.push(lstForcst);
               
               
               var chngLstForcst = new Object();
               chngLstForcst.cfyQOne = 0.00; 
               chngLstForcst.cfyQTwo =  0.00; 
               chngLstForcst.cfyQThree =  0.00; 
               chngLstForcst.cfyQFour =  0.00; 
               chngLstForcst.category = "i9-Change From Last Forecast";
               revProjectionData.push(chngLstForcst);
               
               
               var achvment = new Object();
               achvment.cfyQOne = parseFloat(((foreCast.cfyQOne*100)/target.cfyQOne).toFixed(3));  
               achvment.cfyQTwo =  parseFloat(((foreCast.cfyQTwo*100)/target.cfyQTwo).toFixed(3)); 
               achvment.cfyQThree =  parseFloat(((foreCast.cfyQThree*100)/target.cfyQThree).toFixed(3)); 
               achvment.cfyQFour = parseFloat(((foreCast.cfyQFour*100)/target.cfyQFour).toFixed(3)); 
               achvment.category = "l12-Achievement %";
               revProjectionData.push(achvment);
               
               
               var growthRate = new Object();
               growthRate.cfyQOne = 0.00; 
               growthRate.cfyQTwo =  0.00; 
               growthRate.cfyQThree =  0.00; 
               growthRate.cfyQFour =  0.00; 
               growthRate.category = "m13-Growth Rate (QnQ)";
               revProjectionData.push(growthRate);
               
               
               $scope.revProjectionData = revProjectionData.sort((a, b) => (a.category > b.category) ? 1 : -1);
               
               alert(JSON.stringify($scope.revProjectionData));
               
               foreCast = null;
               gap = null;
               target = null;
               lstForcst = null;
               chngLstForcst = null;
               achvment = null;
               growthRate = null;
  
             }
             
           });
           
           
           
            
             $scope.chartOBForecast = {
            
                   chart: {
                     type: 'column',
                  style: {
                                  fontFamily: 'Bahnschrift'
                              }
                 },
                 plotOptions: {
                   series: {
                       stacking: 'normal',
                       allowPointSelect: true,
                       states: {
                           select: {
                               color: null,
                               borderWidth:5,
                               borderColor:'Blue'
                           }
                       }
                   }
               },

                 title: {
                     text: 'Revenue Projection'
                 },
                 xAxis: {
                     categories: ['Q1', 'Q2', 'Q3', 'Q4']

                 },

                 yAxis: {
                     allowDecimals: false,
                     min: 0,
                     title: {
                         align: 'high'
                       },labels: {
                            overflow: 'justify'
                        }
                 },

                 tooltip: {
                     formatter: function () {
                         return '<b>' + this.x + '</b><br/>' +
                             this.series.name + ': ' + this.y + '<br/>' +
                             'Total: ' + this.point.stackTotal;
                     }

                 },

                 plotOptions: {
                     column: {
                         stacking: 'normal'
                     }
                 },
               legend: {
                                layout: 'vertical',
                                align: 'right',
                                verticalAlign: 'middle',
                                floating: false,
                                borderWidth: 1,
                                backgroundColor:
                                    Highcharts.defaultOptions.legend.backgroundColor || '#FFFFFF',
                                shadow: true
                            },
                            credits: {
                                enabled: false
                            },
                   series: [{
                                name: 'Target',
                                data: targetSeriesData,
                                stack: 'target'
                        
                            }, {
                                name: 'Upside',
                                color: '#ff7f00',
                                data: upsideSeriesData,
                                stack: 'project'
                            }, {
                                name: 'Pipeline',
                                color: '#ffff00',
                                data: pipeSeriesData,
                                stack: 'project'
                            },
                            {
                              name: 'Forecast',
                              color: '#009d00',
                              data: foreCastSeriesData,
                              stack: 'project'
                          }]            
             }     

         }
         
         $scope.loadProjRevEbidtaChrt = function(user) {
           
           //alert($scope.sumArrays([[0, 1, 2], [1, 2, 3, 4], [1, 2]]));
           var userDataPrj = [];
          
           var revData = [];
           var revDataFinal = [];
           var revDataLst = [];
           
           var ebidtaData = [];
           var ebidtaDataPerc = [];
           var ebidtaDataFinal = [];
           var ebidtaDataLst = [];
           
           
           userDataPrj = $scope.loadPmSeriesData(user)[0];
           
           $scope.pnlSummaryData = [];
           
          // $scope.loadPnLSummaryData(user);
          
           angular.forEach(userDataPrj, function (value, key) {
             
              ebidtaData = [parseFloat(value.aprEbi),parseFloat(value.mayEbi),parseFloat(value.junEbi),parseFloat(value.julEbi),parseFloat(value.augEbi)
               ,parseFloat(value.sepEbi),parseFloat(value.octEbi),parseFloat(value.novEbi),parseFloat(value.decEbi),parseFloat(value.janEbi),parseFloat(value.febEbi)
               ,parseFloat(value.marEbi)];
              ebidtaDataLst.push(ebidtaData);
                   
             revData = [parseFloat(value.aprRev),parseFloat(value.mayRev),parseFloat(value.junRev),parseFloat(value.julRev),parseFloat(value.augRev)
               ,parseFloat(value.sepRev),parseFloat(value.octRev),parseFloat(value.novRev),parseFloat(value.decRev),parseFloat(value.janRev),parseFloat(value.febRev)
               ,parseFloat(value.marRev)];
             revDataLst.push(revData);
             
             
           });
           
           ebidtaDataFinal= ebidtaData.map(function(ebitap, i) { return parseFloat(ebitap.toFixed(2)); });
           
           revDataFinal = revData.map(function(revap, i) { return parseFloat(revap.toFixed(2)); });
          
           $scope.chartProjRevenue = {  
                     
                   chart: {
                     zoomType: 'xy',
                     style: {
                       fontFamily: 'Bahnschrift'
                   }
                 },
                 title: {
                     text: 'Revenue & EBIDTA Chart'
                 },
                 xAxis: [{
                     categories: ['Apr', 'May', 'Jun',
                       'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec','Jan', 'Feb', 'Mar'],
                     crosshair: true
                 }],
                 yAxis: [{ // Primary yAxis
                     labels: {
                         format: '{value} %',
                         style: {
                             color: Highcharts.getOptions().colors[1]
                         }
                     },
                     title: {
                         text: 'EBIDTA(%)',
                         style: {
                             color: Highcharts.getOptions().colors[1]
                         }
                     }
                 }, { // Secondary yAxis
                     title: {
                         text: 'Revenue ($)',
                         style: {
                             color: Highcharts.getOptions().colors[0]
                         }
                     },
                     labels: {
                         format: '$ {value} M',
                         style: {
                             color: Highcharts.getOptions().colors[0]
                         }
                     },
                     opposite: true
                 }],
                 tooltip: {
                     shared: true
                 },
                 legend: {
                     layout: 'vertical',
                     align: 'top',
                     x: 200,
                     verticalAlign: 'top',
                     y: 50,
                     floating: true,
                     backgroundColor:
                         Highcharts.defaultOptions.legend.backgroundColor || // theme
                         'rgba(255,255,255,0.25)'
                 },
                 series: [{
                     name: 'Revenue',
                     type: 'column',
                     yAxis: 1,
                     data: revDataFinal,
                     tooltip: {
                         valueSuffix: ' M'
                     }

                 }, {
                     name: 'EBIDTA(%)',
                     type: 'spline',
                     data: ebidtaDataFinal,
                     tooltip: {
                         valueSuffix: '%'
                     }
                 }]
           }
           
           
           
           
        /*   $scope.chartProjRevenue = {
                   
                   chart: {
                     renderTo: 'container',
                     type: 'column',
                     style: {
                       fontFamily: 'Bahnschrift'
                   }
                 },
                 title: {
                     text: 'Revenue Chart'
                 },credits: {
                   enabled: false
                 },
                 yAxis: {
                   title: {
                       text: 'Revenue ($)'
                   },
                   labels: {
                     format: '$ {value} M',
                     style: {
                         color: Highcharts.getOptions().colors[1]
                     }
                 },
               },

               xAxis: {
                 categories: ['Apr', 'May', 'Jun',
                   'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec','Jan', 'Feb', 'Mar'],
                   crosshair: true
               },
               legend: {
                 layout: 'vertical',
                 align: 'right',
                 verticalAlign: 'middle'
             },

                 plotOptions: {
                     column: {
                         depth: 25
                     }
                 },
                 series: [{
                   name: 'Revenue($ M)',
                   data: revDataFinal
                 }]
                   
           } */
           
           $scope.chartProjEbidta = {
                   
                   chart: {
                       style: {
                         fontFamily: 'Bahnschrift'
                     }
                   },
                   title: {
                     text: 'EBIDTA Chart'
                 },credits: {
                   enabled: false
                 },
                 yAxis: {
                     title: {
                         text: 'EBIDTA(%)'
                     },
                     labels: {
                       format: '{value} %',
                       style: {
                           color: Highcharts.getOptions().colors[1]
                       }
                   },
                 },

                 xAxis: {
                   categories: ['Apr', 'May', 'Jun',
                     'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec','Jan', 'Feb', 'Mar'],
                     crosshair: true
                 },

                 legend: {
                     layout: 'vertical',
                     align: 'right',
                     verticalAlign: 'middle'
                 },

                 plotOptions: {
                     series: {
                         label: {
                             connectorAllowed: false
                         },
                      },
                    line: {
                        dataLabels: {
                            enabled: true
                        },
                        enableMouseTracking: false
                    }
                 },

                 series: [{
                     name: 'EBIDTA(%)',
                     data: ebidtaDataFinal
                 }],

                 responsive: {
                     rules: [{
                         condition: {
                             maxWidth: 500
                         },
                         chartOptions: {
                             legend: {
                                 layout: 'horizontal',
                                 align: 'center',
                                 verticalAlign: 'bottom'
                             }
                         }
                     }]
                 }
                   
           }
           
            $scope.chartProjRevEbidta = {
                    
                    chart: {
                      zoomType: 'xy',
                      style: {
                        fontFamily: 'Bahnschrift'
                    }
                  },
                  title: {
                      text: 'Revenue Ebidta map'
                  },
                  xAxis: [{
                      categories: ['Apr', 'May', 'Jun',
                          'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec','Jan', 'Feb', 'Mar'],
                      crosshair: true
                  }],
                  yAxis: [{ // Primary yAxis
                      labels: {
                          format: '{value} %',
                          style: {
                              color: Highcharts.getOptions().colors[1]
                          }
                      },
                      maxPadding: 0,
                      minPadding: 0,
                                floor: 0,
                                ceiling: 100,
                      title: {
                          text: '<span style="font-size: 15px">EBIDTA(%)</span>',
                          style: {
                              color: Highcharts.getOptions().colors[1]
                          }
                      }
                  }, { // Secondary yAxis
                      title: {
                          text: '<span style="font-size: 15px">Revenue</span>',
                          style: {
                              color: Highcharts.getOptions().colors[0]
                          }
                      },
                      labels: {
                          format: '{value} $',
                          style: {
                              color: Highcharts.getOptions().colors[0]
                          }
                      },
                     
                      opposite: true
                  }],
                  tooltip: {
                      shared: true
                  },
                  
                  series: [{
                      name: 'Revenue',
                      type: 'column',
                      yAxis: 1,
                      data: $scope.sumArrays(revDataLst),
                      tooltip: {
                          valueSuffix: ' $'
                      }

                  }, {
                      name: 'EBIDTA',
                      type: 'spline',
                      data: ebidtaDataPerc,
                      tooltip: {
                          valueSuffix: '%'
                      }
                  }] 
                    
            }
 
             $scope.chartOBForecast = {
            
                   chart: {
                     type: 'column',
                  style: {
                                  fontFamily: 'Bahnschrift'
                              }
                 },
                 plotOptions: {
                   series: {
                       stacking: 'normal',
                       allowPointSelect: true,
                       states: {
                           select: {
                               color: null,
                               borderWidth:5,
                               borderColor:'Blue'
                           }
                       }
                   }
               },

                 title: {
                     text: 'Revenue Projection'
                 },
                 xAxis: {
                     categories: ['Q1', 'Q2', 'Q3', 'Q4']

                 },

                 yAxis: {
                     allowDecimals: false,
                     min: 0,
                     title: {
                         align: 'high'
                       },labels: {
                            overflow: 'justify'
                        }
                 },

                 tooltip: {
                     formatter: function () {
                         return '<b>' + this.x + '</b><br/>' +
                             this.series.name + ': ' + this.y + '<br/>' +
                             'Total: ' + this.point.stackTotal;
                     }

                 },

                 plotOptions: {
                     column: {
                         stacking: 'normal'
                     }
                 },
               legend: {
                                layout: 'vertical',
                                align: 'right',
                                verticalAlign: 'middle',
                                floating: false,
                                borderWidth: 1,
                                backgroundColor:
                                    Highcharts.defaultOptions.legend.backgroundColor || '#FFFFFF',
                                shadow: true
                            },
                            credits: {
                                enabled: false
                            },
                   series: [{
                                name: 'Target',
                                data: [3.38, 3.38, 3.38, 3.38],
                                stack: 'target'
                        
                            }, {
                                name: 'Upside',
                                data: [0.00, 0.06, 0.18, 0.12],
                                stack: 'project'
                            }, {
                                name: 'Pipeline',
                                data: [0.00, 0.06, 0.18, 0.12],
                                stack: 'project'
                            },
                            {
                              name: 'Forecast',
                              data: [1.69, 2.15, 2.32, 2.02],
                              stack: 'project'
                          }]

            
             }     

         }
         

         $scope.sortByMonth = function(monthArr) {
           
           var months = ["April", "May", "June",
             "July", "August", "September", "October", "November", "December","January", "February", "March"];
             arr.sort(function(a, b){
                 return months.indexOf(a.values.Month.displayValue)
                      - months.indexOf(b.values.Month.displayValue);
             });

         }
         
         $scope.showProjectChrt = function() {
           
           $scope.showProjects=true;
           $scope.showAssociates=false;
           $scope.showPnL=false;
           $scope.showOB=false;
           
         }
         
         $scope.showAssociateChrt = function() {
           
           $scope.showAssociates=true;
           $scope.showProjects=false;
           $scope.showPnL=false;
           $scope.showOB=false;
           
         }
         
         $scope.showPnLChrt = function() {
           
           $scope.showPnL=true;
           $scope.showProjects=false;
           $scope.showAssociates=false;
           $scope.showOB=false;
           
         }
         
         $scope.showOdBook = function() {
           
           $scope.showOB=true;
           $scope.showPnL=false;
           $scope.showProjects=false;
           $scope.showAssociates=false;
           
         }
         
         $scope.showInProgress = function() {
           
           alert("I am in progress...");
           
         }
         
		 $scope.sortByValue = function(jsObj){
  		   
		     var sortedArray = [];
  		    for(var i in jsObj)
  		    {
  		        sortedArray.push([jsObj[i],i]);
  		    }
  		    
  		    return sortedArray.sort();
		 }
		 
		 $scope.reverseByValue = function(jsObj){
       
       var sortedArray = [];
        for(var i in jsObj)
        {
            // Push each JSON Object entry in array by [value, key]
            sortedArray.push([i,jsObj[i]]);
        }
        return sortedArray.sort().reverse();
   }
		 
		 $scope.sumArrays = function(arrays) {
		
		   const n = arrays.reduce((max, xs) => Math.max(max, xs.length), 0);
		   const result = Array.from({ length: n });
		   return result.map((_, i) => arrays.map(xs => xs[i] || 0).reduce((sum, x) => { sum = sum + x ;return parseFloat(sum.toFixed(2))}));
		 }
		 
		 $scope.showProgress=function(){
		   
		   $scope.loaderds =true;
       // $scope.showSaveBtn11=true;
     }
		 
		 $scope.hideProgress=function(){
		  $scope.loaderds =false;
       // $scope.showSaveBtn11=true;
     }
		 
		 $scope.goOrderBook=function(){    
       $state.go('odbook');
      
    }
        	    
	    $scope.getselectval = function (selData) {
       
         $scope.projects=[];
         $scope.showdropdown=true; 
         var managers = new Object();
          managers.pid="allprj";
          managers.pname="---All Projects---";
          $scope.projects.push(managers);
           angular.forEach($scope.user.projMasterData, function (valueOut, keyOut) { 

                   if(valueOut.pmId==selData.pid){

                     var managers = new Object();
                     managers.pid=valueIn.projectId;
                     managers.pname=valueIn.projectDescription;
                     $scope.projects.push(managers);
      
                   }           
           }); 
           $scope.selProject=$scope.projects[0];
        };
        
    });
