angular.module('PmoxApp')

    .controller('HomeController', function ($http, $scope, $state,AuthService,$rootScope) {
        $scope.user = AuthService.user;
     
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
          $scope.pgmName = '';
          $scope.spgmName = '';
          $scope.prjNme = '';
          $scope.pmName = '';
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
          $scope.filterLevelMap = [];
          
          $scope.cfyQOne = '';
          $scope.cfyQTwo = '';
          $scope.cfyQThree = '';
          $scope.cfyQFour = '';
          
          $scope.init = function () {
            $scope.disableTabs=true;  
                      
           $scope.user = $scope.loadProjectMasterData(AuthService.user)[0] ; 
           $scope.loadFiltersWithStatusData($scope.user.projMasterData);
          
           $scope.totalProjectCount = $scope.projects.length-1;
           $scope.totalOffShoreCount = $scope.user.totalOffShoreCount;
           $scope.totalOnShoreCount = $scope.user.totalOnShoreCount;
           $scope.loadProjRevProjectionData($scope.user); 
           $scope.loadPnLSummaryData($scope.user);
           $scope.loadPricingMdlData($scope.user.projMasterData);
           $scope.loadProjStatus($scope.user.projMasterData);
           $scope.loadProjManaged($scope.user.projMasterData); 
           $scope.loadProjTech($scope.user.projMasterData);
           $scope.loadProjType($scope.user.projMasterData);
           $scope.loadProjLocChrt($scope.user.resourceMap);
           
           
         };        
         
         $scope.loadProjectMasterData = function(userFrSearch){
          
           var usr = [];
           $.ajax({
             url: "api/projects/getprojectsfruser",
             error: function (e) {
               alert('Invalid Result set for the request.'+JSON.stringify(e))
             },
             dataType: "json",
             contentType: 'application/json; charset=utf-8',
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
                              
                $("#alertMsg").html("The required data is not available for the selected user.");                          
                $('#alertModal').modal("show");
             },
             dataType: "json",
             contentType: 'application/json; charset=utf-8',
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
         
         $scope.loadPnLSummaryData = function(userFrSearch){
           
           var ebidtaData = [];
           var revData = [];
           var ebidtaDataFinal = [];
           var revDataFinal = [];
           var revenue = new Object();
           var ebidta = new Object();
           var ebidtap = new Object();
           
           $.ajax({
             url: "api/projects/getPnLSummary",
             error: function (e) {
               alert('Invalid Result set for the request.'+JSON.stringify(e))
             },
             dataType: "json",
             contentType: 'application/json; charset=utf-8',
             type: "POST",
             async: false,
             cache: false,
             data: JSON.stringify(userFrSearch),
             timeout: 30000,
             crossDomain: true,
             success: function (data) {               
             
               $scope.pnlSummaryData = data;
               
               angular.forEach(data, function (value, key) {
                 
                 if(value.attribute==='REVENUE'){
                                     
                   revenue.monApr = value.monApr; 
                   revenue.monMay = value.monMay; 
                   revenue.monJun = value.monJun; 
                   revenue.monJul = value.monJul; 
                   revenue.monAug = value.monAug; 
                   revenue.monSep = value.monSep; 
                   revenue.monOct = value.monOct; 
                   revenue.monNov = value.monNov; 
                   revenue.monDec = value.monDec; 
                   revenue.monJan = value.monJan; 
                   revenue.monFeb = value.monFeb; 
                   revenue.monMar = value.monMar; 
                   revenue.total = value.total; 
                   $scope.totalRevenue = (value.total/1000000).toFixed(3);
                   revData= [value.monApr/1000000,value.monMay/1000000,value.monJun/1000000,value.monJul/1000000,value.monAug/1000000,value.monSep/1000000,
                     value.monOct/1000000,value.monNov/1000000,value.monDec/1000000,value.monJan/1000000,value.monFeb/1000000,value.monMar/1000000];
                  
                 }
                   if(value.attribute==='EBIDTA'){
                     
                     ebidta.monApr = value.monApr; 
                     ebidta.monMay = value.monMay; 
                     ebidta.monJun = value.monJun; 
                     ebidta.monJul = value.monJul; 
                     ebidta.monAug = value.monAug; 
                     ebidta.monSep = value.monSep; 
                     ebidta.monOct = value.monOct; 
                     ebidta.monNov = value.monNov; 
                     ebidta.monDec = value.monDec; 
                     ebidta.monJan = value.monJan; 
                     ebidta.monFeb = value.monFeb; 
                     ebidta.monMar = value.monMar; 
                     ebidta.total  = value.total;   
                    }
                 
                 });
             }
             
           });
           
            ebidtap.attribute = "EBIDTA(%)"
            ebidtap.monApr = revenue.monApr==0 ? 0 : (ebidta.monApr*100/revenue.monApr); 
            ebidtap.monMay = revenue.monMay==0 ? 0 : (ebidta.monMay*100/revenue.monMay) ; 
            ebidtap.monJun = revenue.monJun==0 ? 0 : (ebidta.monJun*100/revenue.monJun) ;
            ebidtap.monQ1Tot = (revenue.monApr+revenue.monMay+revenue.monJun)==0 ? 0 : ((ebidta.monApr+ebidta.monMay+ebidta.monJun)*100/(revenue.monApr+revenue.monMay+revenue.monJun));
            ebidtap.monJul = revenue.monJul==0 ? 0 : (ebidta.monJul*100/revenue.monJul) ; 
            ebidtap.monAug = revenue.monAug==0 ? 0 : (ebidta.monAug*100/revenue.monAug) ; 
            ebidtap.monSep = revenue.monSep==0 ? 0 : (ebidta.monSep*100/revenue.monSep) ; 
            ebidtap.monQ2Tot = (revenue.monJul+revenue.monAug+revenue.monSep)==0 ? 0 : ((ebidta.monJul+ebidta.monAug+ebidta.monSep)*100/(revenue.monJul+revenue.monAug+revenue.monSep));
            ebidtap.monOct = revenue.monOct==0 ? 0 : (ebidta.monOct*100/revenue.monOct) ; 
            ebidtap.monNov = revenue.monNov==0 ? 0 : (ebidta.monNov*100/revenue.monNov) ; 
            ebidtap.monDec = revenue.monDec==0 ? 0 : (ebidta.monDec*100/revenue.monDec) ; 
            ebidtap.monQ3Tot = (revenue.monOct+revenue.monNov+revenue.monDec)==0 ? 0 : ((ebidta.monOct+ebidta.monNov+ebidta.monDec)*100/(revenue.monOct+revenue.monNov+revenue.monDec));
            ebidtap.monJan = revenue.monJan==0 ? 0 : (ebidta.monJan*100/revenue.monJan) ; 
            ebidtap.monFeb = revenue.monFeb==0 ? 0 : (ebidta.monFeb*100/revenue.monFeb) ; 
            ebidtap.monMar = revenue.monMar==0 ? 0 : (ebidta.monMar*100/revenue.monMar) ; 
            ebidtap.monQ4Tot = (revenue.monJan+revenue.monFeb+revenue.monMar)==0 ? 0 : ((ebidta.monJan+ebidta.monFeb+ebidta.monMar)*100/(revenue.monJan+revenue.monFeb+revenue.monMar));
            ebidtap.total  = revenue.total== 0 ? 0 : (ebidta.total*100/revenue.total) ; 
 
                       
            $scope.totalEbidta = ebidtap.total.toFixed(2);
            ebidtaData=[ebidtap.monApr ==0 ? null : parseFloat(ebidtap.monApr.toFixed(2)) ,ebidtap.monMay==0 ? null : parseFloat(ebidtap.monMay.toFixed(2)),ebidtap.monJun==0 ? null : parseFloat(ebidtap.monJun.toFixed(2)),ebidtap.monJul==0 ? null : parseFloat(ebidtap.monJul.toFixed(2)),
            ebidtap.monAug==0 ? null : parseFloat(ebidtap.monAug.toFixed(2)),ebidtap.monSep==0 ? null : parseFloat(ebidtap.monSep.toFixed(2)),ebidtap.monOct==0 ? null : parseFloat(ebidtap.monOct.toFixed(2)),ebidtap.monNov==0 ? null : parseFloat(ebidtap.monNov.toFixed(2)),ebidtap.monDec==0 ? null : parseFloat(ebidtap.monDec.toFixed(2)),
            ebidtap.monJan==0 ? null : parseFloat(ebidtap.monJan.toFixed(2)),ebidtap.monFeb==0 ? null : parseFloat(ebidtap.monFeb.toFixed(2)),ebidtap.monMar==0 ? null : parseFloat(ebidtap.monMar.toFixed(2))];
          
            $scope.pnlSummaryData.push(ebidtap);
          
           ebidtaDataFinal= ebidtaData.map(function(ebitap, i) { if(ebitap!=null){return parseFloat(ebitap.toFixed(2));} });
           
           revDataFinal = revData.map(function(revap, i) { return parseFloat(revap.toFixed(2)); });
                   
           $scope.chartProjRevenue = {  
                   
                   chart: {
                     zoomType: 'xy',
                     style: {                    
                       fontFamily: 'Bahnschrift'
                   }
                 },
                 title: {
                     text: 'Revenue vs EBIDTA',
                     style: {                    
                        fontSize: '15px',
                       
                   }
                                       
                     
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
                     //layout: 'vertical',
                     align: 'center',
                     verticalAlign: 'bottom',
                     floating: false,
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
                     data: ebidtaData,
                     color: '#ffbf00',
                     tooltip: {
                         valueSuffix: ''
                     }
                 }]
           
             }
           
         }
         
         $scope.getFilterData=function(filtrObj,roleFilter){
                      
           var userDataPrj = [];
             
             switch(roleFilter) {
             case 'SBU':
               $scope.user.sbuName = filtrObj.name;
               break;
             case 'IBG':
               $scope.user.ibgName = filtrObj.name;
               break;
             case 'IBU':
               $scope.user.ibuName = filtrObj.name;
               break;
             case 'SALES':
               $scope.user.spgmName = filtrObj.name;
               $scope.user.pmName = '---All PMs---';
               $scope.user.prjNme = '---All Projects---';
               break;
             case 'PGM':
               $scope.user.pgmName = filtrObj.name;
               $scope.user.pmName = '---All PMs---';
               $scope.user.prjNme = '---All Projects---';
               break;
             case 'PM':
               $scope.user.pmName = filtrObj.pmname;
               $scope.user.prjNme = '---All Projects---';
               break;
             case 'PRJ':
               $scope.user.prjNme = filtrObj.pname;
               break;
           }; 
           $scope.user.filterRoleSel=roleFilter;
           
           userDataPrj = $scope.loadProjectMasterData($scope.user)[0];
           $scope.loadFiltersWithStatusData(userDataPrj.projMasterData,roleFilter);
           if(roleFilter === 'PRJ' && filtrObj.pname != '---All Projects---'){
             $scope.totalProjectCount = 1;
           }else{
             $scope.totalProjectCount = $scope.projects.length-1;
           }
                      
           $scope.totalOffShoreCount = userDataPrj.totalOffShoreCount;
           $scope.totalOnShoreCount = userDataPrj.totalOnShoreCount;
           
           $scope.loadProjRevProjectionData($scope.user);  
           $scope.loadPnLSummaryData($scope.user);
           $scope.loadPricingMdlData(userDataPrj.projMasterData);
           $scope.loadProjStatus(userDataPrj.projMasterData);         
           $scope.loadProjManaged(userDataPrj.projMasterData);          
           $scope.loadProjTech(userDataPrj.projMasterData);         
           $scope.loadProjType(userDataPrj.projMasterData);          
           $scope.loadProjLocChrt(userDataPrj.resourceMap);
           
         }
         
         $scope.loadFiltersWithStatusData = function(projMasterData,roleFilter){
                    
          switch(roleFilter) {
             case 'SBU':
               $scope.getDistinctIbg(projMasterData);
               $scope.getDistinctIbu(projMasterData);
               $scope.getDistinctSales(projMasterData);
               $scope.getDistinctPGM(projMasterData);
               $scope.getDistinctPM(projMasterData);
               $scope.getDistinctProject(projMasterData);
               break;
             case 'IBG':
               $scope.getDistinctIbu(projMasterData);
               $scope.getDistinctSales(projMasterData);
               $scope.getDistinctPGM(projMasterData);
               $scope.getDistinctPM(projMasterData);
               $scope.getDistinctProject(projMasterData);
               break;
             case 'IBU':
               $scope.getDistinctSales(projMasterData);
               $scope.getDistinctPGM(projMasterData);
               $scope.getDistinctPM(projMasterData);
               $scope.getDistinctProject(projMasterData);
               break;
             case 'SALES':
               
              /* angular.forEach($scope.pgManagers, function (valueOut, keyOut) {
  
                 if(valueOut.id==='allpgm'){
                   $scope.pgmdata=valueOut;
                 }
               
               });  */
               $scope.getDistinctPM(projMasterData);
               $scope.getDistinctProject(projMasterData);
               break;
             case 'PGM':
               $scope.getDistinctPM(projMasterData);
               $scope.getDistinctProject(projMasterData);
               break;
             case 'PM':
               $scope.getDistinctProject(projMasterData);
               break;
             case 'PRJ':
              // $scope.user.prjNme = filtrObj.name;
               break;
             default : 
                $scope.getDistinctSbu(projMasterData);
                $scope.getDistinctIbg(projMasterData);
                $scope.getDistinctIbu(projMasterData);
                $scope.getDistinctSales(projMasterData);
                $scope.getDistinctPGM(projMasterData);
                $scope.getDistinctPM(projMasterData);
                $scope.getDistinctProject(projMasterData);
           };                       
         }
         
         
         $scope.getDistinctSbu=function(projMasterData){
           
           var sbu = new Object();
           //$scope.sbuNames = [];
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
                
             });  
           }
           
           $scope.sbuNames = $scope.sbuNames.sort((a, b) => (a.name > b.name) ? 1 : -1);
         
         }; 
         
         $scope.getDistinctIbg=function(projMasterData){
           
          // $scope.ibgNames = [];
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
                              
             });  
           }
           
           $scope.ibgNames = $scope.ibgNames.sort((a, b) => (a.name > b.name) ? 1 : -1);
         
         }; 
         
         
         
         $scope.getDistinctIbu=function(projMasterData){
           
           //$scope.ibuNames = [];
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
               
             });  
           }
           
           $scope.ibuNames = $scope.ibuNames.sort((a, b) => (a.name > b.name) ? 1 : -1);
         
         }; 
         
         $scope.getDistinctSales=function(projMasterData){
           
           $scope.sPgManagers = [];
           var sPgm = new Object();
           sPgm.id="allspgm";
           sPgm.name="---All Sales Mangers---";
           $scope.sPgManagers.push(sPgm);
           
           var distinctsPgm = [...new Set(projMasterData.map(spgm => spgm.sPgmId+':'+spgm.sPgmName))];
                
           
           if(distinctsPgm.length==1){
             var spgmArr = distinctsPgm[0].split(':');
             var spgm = new Object();
             spgm.id=spgmArr[0];
             spgm.name=spgmArr[1];
             $scope.sPgManagers = [];
             if(spgm.name!='BUFFER'){
               $scope.sPgManagers.push(spgm);
             }
             $scope.sPgmdata=spgm;
             
           }else{
                        
              angular.forEach(distinctsPgm, function (valueOut, keyOut) {
                
                  var pgmArr = valueOut.split(':');
                  var spgm = new Object();
                  spgm.id=pgmArr[0];
                  spgm.name=pgmArr[1];
                  if(spgm.name!='BUFFER'){
                    $scope.sPgManagers.push(spgm);
                  }
                  $scope.sPgmdata=$scope.sPgManagers[0];
                
              });  
           }
           
           $scope.sPgManagers = $scope.sPgManagers.sort((a, b) => (a.name > b.name) ? 1 : -1);
         
         }; 
         
         $scope.getDistinctPGM=function(projMasterData){
           
           $scope.pgManagers = [];
           var pgm = new Object();
           pgm.id="allpgm";
           pgm.name="---All PGMs---";
           $scope.pgManagers.push(pgm);
           
           var distinctPgm = [...new Set(projMasterData.map(pgm => pgm.pgmId+':'+pgm.pgmName))];
           
           if(distinctPgm.length==1){
             var pgmArr = distinctPgm[0].split(':');
             var pgm = new Object();
             pgm.id=pgmArr[0];
             pgm.name=pgmArr[1];
             $scope.pgManagers = [];
             $scope.pgManagers.push(pgm);
             $scope.pgmdata=pgm;
             
           }else{
           
             angular.forEach(distinctPgm, function (valueOut, keyOut) {
               
                 var pgmArr = valueOut.split(':');
                 var pgm = new Object();
                 pgm.id=pgmArr[0];
                 pgm.name=pgmArr[1];
                 $scope.pgManagers.push(pgm);
                 $scope.pgmdata=$scope.pgManagers[0];
               
             });  
           }
           
           $scope.pgManagers = $scope.pgManagers.sort((a, b) => (a.name > b.name) ? 1 : -1);
         
         }; 
         
         $scope.getDistinctPM=function(projMasterData){
           
           $scope.manNames =[];
          
           var managers = new Object();
           managers.pid="allpm";
           managers.pmname="---All PMs---";
           $scope.manNames.push(managers);
           $scope.pmData=$scope.manNames[0];
           
           var distinctPm = [...new Set(projMasterData.map(pm => pm.pmId+':'+pm.pmName))];
           
           if(distinctPm.length==1){
             var pgmArr = distinctPm[0].split(':');
             var pgm = new Object();
             pgm.pid=pgmArr[0];
             pgm.pmname=pgmArr[1];
             $scope.manNames = [];
             $scope.manNames.push(pgm);
             $scope.pmData=pgm;
             
           }else{
           
             angular.forEach(distinctPm, function (valueOut, keyOut) {
               
                 var pmArr = valueOut.split(':');
                 var managers = new Object();
                 managers.pid=pmArr[0];
                 managers.pmname=pmArr[1];
                 $scope.manNames.push(managers);
                           
             });  
           }
           
           $scope.manNames = $scope.manNames.sort((a, b) => (a.pmname > b.pmname) ? 1 : -1);
         
         }; 
                  
         $scope.getDistinctProject=function(projMasterData){
           
           $scope.projects=[];
           var projects = new Object();
           projects.pid="allprj";
           projects.pname="---All Projects---";
           $scope.projects.push(projects);
           $scope.selProject=$scope.projects[0];
           
           var distinctProject = [...new Set(projMasterData.map(project => project.projectId+':'+project.projectDescription))];
           
          
           if(distinctProject.length==1){
             var pgmArr = distinctProject[0].split(':');
             var pgm = new Object();
             pgm.pid=pgmArr[0];
             pgm.pname=pgmArr[1];
             $scope.projects = [];
             $scope.projects.push(pgm);
             $scope.selProject=pgm;
             
           }else{
           
             angular.forEach(distinctProject, function (valueOut, keyOut) {
               
                 var projectArr = valueOut.split(':');
                 var projects = new Object();
                 projects.pid=projectArr[0];
                 projects.pname=projectArr[1];
                 $scope.projects.push(projects);
               
               
             });  
           }
           
           $scope.projects = $scope.projects.sort((a, b) => (a.pname > b.pname) ? 1 : -1);
         
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
                    text: 'HTR Distribution',
                    style: {                    
                        fontSize: '15px',                      
                   }                                     
                },
                credits: {
                  enabled: false
                },
                tooltip: {
                  // Nice and easy number formatting
                  
                  shared: true
              },
                plotOptions: {
                    series: {
                      dataLabels: {
                          enabled: true,
                          inside: true,
                          align: "center",
                          verticalAlign: "middle",
                          format: '<b>{point.name}</b> ({point.y} , {point.percentage:.1f}%)',
                        
                          softConnector: true
                      }
                      
                  },
                  center: ['50%', '50%'],
                  width: '80%'
                },
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
                                      inside: true,
                                      format: '<b>{point.name}</b> ({point.y:,.0f}%)'
                                  },
                                  center: ['50%', '50%'],
                                  width: '100%'
                              }
                          }
                      }
                  }]
                }
              };
          
                   
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
           
           var uniqsCntry = resMapData.reduce((acc, val) => {
             acc[val.country] = acc[val.country] === undefined ? 1 : acc[val.country] += 1;
             return acc;
           }, {});
           
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
               
                $("#alertMsg").html("The required data is not available for the selected user.");                          
                $('#alertModal').modal("show");
            
             },
             dataType: "json",
             contentType: 'application/json; charset=utf-8',
             type: "POST",
             async: false,
             cache: false,
             data: JSON.stringify(user),
             timeout: 30000,
             crossDomain: true,
             success: function (data) {               
             
               console.log(JSON.stringify(data));

               var odb = new Object();
               var ren = new Object();
               var commit = new Object();
               var ups = new Object();
               var pipe = new Object();
               var strup = new Object();
               var target = new Object();
               target.cfyQOne = 0.00; 
               target.cfyQTwo =  0.00; 
               target.cfyQThree =  0.00; 
               target.cfyQFour =  0.00; 
               target.colorCode = true;
               target.category = "a-Target";
               targetSeriesData= [target.cfyQOne,target.cfyQTwo,target.cfyQThree,target.cfyQFour];
               revProjectionData.push(target);
               
               odb.cfyQOne = 0.00; 
               odb.cfyQTwo =  0.00; 
               odb.cfyQThree = 0.00; 
               odb.cfyQFour =  0.00; 
               odb.category = 'b-Order Book & Pending PO (OB)';
               revProjectionData.push(odb);
               
               ren.cfyQOne = 0.00; 
               ren.cfyQTwo =  0.00;
               ren.cfyQThree = 0.00;
               ren.cfyQFour =  0.00;
               ren.category = 'c-Expected Renewal';
               revProjectionData.push(ren);
               
               commit.cfyQOne = 0.00; 
               commit.cfyQTwo =  0.00; 
               commit.cfyQThree = 0.00; 
               commit.cfyQFour =  0.00; 
               commit.category = 'd-Commit(P4) (C)'; 
               revProjectionData.push(commit);
               
               strup.cfyQOne = 0.00; 
               strup.cfyQTwo =  0.00; 
               strup.cfyQThree = 0.00; 
               strup.cfyQFour =  0.00; 
               strup.category = 'e-Strong Upside (P3.1) (SU)'; 
               revProjectionData.push(strup);
               
               ups.cfyQOne = 0.00; 
               ups.cfyQTwo = 0.00; 
               ups.cfyQThree = 0.00; 
               ups.cfyQFour =  0.00; 
               ups.colorCode = true;
               ups.category = 'j-Upside(P3)'; 
               upsideSeriesData = [ups.cfyQOne,ups.cfyQTwo,ups.cfyQThree,ups.cfyQFour];
               revProjectionData.push(ups);
               
               pipe.cfyQOne = 0.00;  
               pipe.cfyQTwo = 0.00; 
               pipe.cfyQThree = 0.00; 
               pipe.cfyQFour = 0.00; 
               pipe.colorCode = true;
               pipe.category = 'k-Pipeline(P0 to P2)';
               revProjectionData.push(pipe);
               
              var counter =1;
               angular.forEach(data, function (value, key) {
                 
                     
                 if(value.category==='a-Target'){
                   
                   revProjectionData = revProjectionData.filter( obj => obj.category !== value.category);
                   
                   target.cfyQOne = value.cfyQOne; 
                   target.cfyQTwo =  value.cfyQTwo;
                   target.cfyQThree = value.cfyQThree;
                   target.cfyQFour =  value.cfyQFour;
                   target.category = value.category;
                   target.colorCode = true;
                   revProjectionData.push(target);
                   targetSeriesData= [target.cfyQOne,target.cfyQTwo,target.cfyQThree,target.cfyQFour];
                   //alert('2222----'+JSON.stringify(revProjectionData));
                 }
                 if(value.category==='b-Order Book & Pending PO (OB)'){
                       
                       revProjectionData = revProjectionData.filter( obj => obj.category !== value.category);
                       
                       odb.cfyQOne = value.cfyQOne; 
                       odb.cfyQTwo =  value.cfyQTwo;
                       odb.cfyQThree = value.cfyQThree;
                       odb.cfyQFour =  value.cfyQFour;
                       odb.category = value.category;
                       revProjectionData.push(odb);
                       //alert('2222----'+JSON.stringify(revProjectionData));
                     }
                     if(value.category==='c-Expected Renewal'){
                       revProjectionData = revProjectionData.filter( obj => obj.category !== value.category);
                         
                         ren.cfyQOne = value.cfyQOne; 
                         ren.cfyQTwo =  value.cfyQTwo;
                         ren.cfyQThree = value.cfyQThree;
                         ren.cfyQFour =  value.cfyQFour;
                         ren.category = value.category;
                         revProjectionData.push(ren);
                     }
                     if( value.category==='d-Commit(P4) (C)'){
                       revProjectionData = revProjectionData.filter( obj => obj.category !== value.category);
                       
                       commit.cfyQOne = value.cfyQOne; 
                       commit.cfyQTwo =  value.cfyQTwo;
                       commit.cfyQThree = value.cfyQThree;
                       commit.cfyQFour =  value.cfyQFour;
                       commit.category = value.category;
                       revProjectionData.push(commit);
                   }
                     if( value.category==='e-Strong Upside (P3.1) (SU)'){
                       revProjectionData = revProjectionData.filter( obj => obj.category !== value.category);
                      
                       strup.cfyQOne = value.cfyQOne; 
                       strup.cfyQTwo =  value.cfyQTwo;
                       strup.cfyQThree = value.cfyQThree;
                       strup.cfyQFour =  value.cfyQFour;
                       strup.category = value.category;
                       revProjectionData.push(strup);
                   }
                     if(value.category==='j-Upside(P3)'){
                       revProjectionData = revProjectionData.filter( obj => obj.category !== value.category);
                      
                       ups.cfyQOne = value.cfyQOne; 
                       ups.cfyQTwo =  value.cfyQTwo;
                       ups.cfyQThree = value.cfyQThree;
                       ups.cfyQFour =  value.cfyQFour;
                       ups.colorCode = true;
                       ups.category = value.category;
                       upsideSeriesData = [value.cfyQOne,value.cfyQTwo,value.cfyQThree,value.cfyQFour];
                       revProjectionData.push(ups);
                   }
                     if(value.category==='k-Pipeline(P0 to P2)'){
                       revProjectionData = revProjectionData.filter( obj => obj.category !== value.category);
                       
                       pipe.cfyQOne = value.cfyQOne; 
                       pipe.cfyQTwo =  value.cfyQTwo;
                       pipe.cfyQThree = value.cfyQThree;
                       pipe.cfyQFour =  value.cfyQFour;
                       pipe.colorCode = true;
                       pipe.category = value.category;
                       pipeSeriesData = [value.cfyQOne,value.cfyQTwo,value.cfyQThree,value.cfyQFour];
                       revProjectionData.push(pipe);
                   }
                     
               });
               
               var foreCast = new Object();
               foreCast.cfyQOne = parseFloat((odb.cfyQOne + ren.cfyQOne + commit.cfyQOne + strup.cfyQOne).toFixed(3));
               foreCast.cfyQTwo =  parseFloat((odb.cfyQTwo + ren.cfyQTwo + commit.cfyQTwo + strup.cfyQTwo).toFixed(3));
               foreCast.cfyQThree = parseFloat((odb.cfyQThree + ren.cfyQThree + commit.cfyQThree + strup.cfyQThree).toFixed(3));
               foreCast.cfyQFour =  parseFloat((odb.cfyQFour + ren.cfyQFour + commit.cfyQFour + strup.cfyQFour).toFixed(3));
               foreCast.colorCode = true;
               foreCast.category = "f-ForeCast";
               foreCastSeriesData= [foreCast.cfyQOne,foreCast.cfyQTwo,foreCast.cfyQThree,foreCast.cfyQFour];
               revProjectionData.push(foreCast);
               
               $scope.cfyQOne = foreCast.cfyQOne;
               $scope.cfyQTwo = foreCast.cfyQTwo;
               $scope.cfyQThree = foreCast.cfyQThree;
               $scope.cfyQFour = foreCast.cfyQFour;
                              
               var gap = new Object();
               gap.cfyQOne = parseFloat((target.cfyQOne - foreCast.cfyQOne).toFixed(3)); 
               gap.cfyQTwo = parseFloat((target.cfyQTwo - foreCast.cfyQTwo).toFixed(3)) ;
               gap.cfyQThree = parseFloat((target.cfyQThree - foreCast.cfyQThree).toFixed(3));
               gap.cfyQFour = parseFloat((target.cfyQFour - foreCast.cfyQFour).toFixed(3)) ;
               gap.category = "g-Gap";
               revProjectionData.push(gap);
                              
               var lstForcst = new Object();
               lstForcst.cfyQOne = 0.00; 
               lstForcst.cfyQTwo =  0.00; 
               lstForcst.cfyQThree =  0.00; 
               lstForcst.cfyQFour =  0.00; 
               lstForcst.category = "h-Last Forecast";
               revProjectionData.push(lstForcst);
                              
               var chngLstForcst = new Object();
               chngLstForcst.cfyQOne = 0.00; 
               chngLstForcst.cfyQTwo =  0.00; 
               chngLstForcst.cfyQThree = 0.00; 
               chngLstForcst.cfyQFour =  0.00; 
               chngLstForcst.category = "i-Change From Last Forecast";
               revProjectionData.push(chngLstForcst);
                              
               var achvment = new Object();
               achvment.cfyQOne = parseFloat(((foreCast.cfyQOne*100)/target.cfyQOne).toFixed(3));  
               achvment.cfyQTwo =  parseFloat(((foreCast.cfyQTwo*100)/target.cfyQTwo).toFixed(3)); 
               achvment.cfyQThree =  parseFloat(((foreCast.cfyQThree*100)/target.cfyQThree).toFixed(3)); 
               achvment.cfyQFour = parseFloat(((foreCast.cfyQFour*100)/target.cfyQFour).toFixed(3)); 
               achvment.category = "l-Achievement %";
               revProjectionData.push(achvment);
               
               var growthRate = new Object();
               growthRate.cfyQOne = 0.00; 
               growthRate.cfyQTwo =  0.00; 
               growthRate.cfyQThree = 0.00; 
               growthRate.cfyQFour =  0.00; 
               growthRate.category = "m-Growth Rate (QnQ)";
               revProjectionData.push(growthRate);
                             
               $scope.revProjectionData = revProjectionData.sort((a, b) => (a.category > b.category) ? 1 : -1);
               
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
                     text: 'Revenue Projection',
                     style: {                    
                        fontSize: '15px',
                       
                   }
                 },
                 xAxis: {
                     categories: ['CFY-Q1', 'CFY-Q2', 'CFY-Q3', 'CFY-Q4']

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
                             this.series.name + ': ' + this.y + '<br/>' ;
                     }

                 },

                 plotOptions: {
                     column: {
                         stacking: 'normal'
                     }
                 },
               legend: {
                                //layout: 'vertical',
                                align: 'center',
                                verticalAlign: 'bottom',
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
                        
                            },  {
                                name: 'Pipeline',
                                color: '#bf00ff',
                                data: pipeSeriesData,
                                stack: 'project'
                            },{
                              name: 'Upside',
                              color: '#ffbf00',
                              data: upsideSeriesData,
                              stack: 'project'
                          },
                            {
                              name: 'Forecast',
                              color: '#00ffbf',
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
           
           $scope.loadPnLSummaryData(user);
          
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
                     text: 'Revenue vs EBIDTA'
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
                     //layout: 'vertical',
                     align: 'center',
                     verticalAlign: 'bottom',
                     floating: false,
                     backgroundColor:
                         Highcharts.defaultOptions.legend.backgroundColor || // theme
                         'rgba(255,255,255,0.25)'
                 },credits: {
                   enabled: false
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
                     //layout: 'vertical',
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
           
           if($scope.showOB){
             $scope.init();
           }
           $scope.showProjects=true;
           $scope.showAssociates=false;
           $scope.showPnL=false;
           $scope.showOB=false;
           
           if($scope.showProjects=true){
               document.getElementById('projectIcon').style.backgroundColor = '#ffc20d';
               document.getElementById('pnlIcon').style.backgroundColor = '#838ba3';  
                document.getElementById('revIcon').style.backgroundColor = '#838ba3';
               
           }
           
         }
         
         $scope.showAssociateChrt = function() {
           
           if($scope.showOB){
             $scope.init();
           }
           $scope.showAssociates=true;
           $scope.showProjects=false;
           $scope.showPnL=false;
           $scope.showOB=false;
           
         }
         
         $scope.showPnLChrt = function() {
         
         if($scope.showOB){
             $scope.init();
           }
           
           $scope.showPnL=true;
           $scope.showProjects=false;
           $scope.showAssociates=false;
           $scope.showOB=false;           
           
           if($scope.showPnL=true){
               document.getElementById('pnlIcon').style.backgroundColor = '#ffc20d'; 
               document.getElementById('projectIcon').style.backgroundColor = '#838ba3'; 
               document.getElementById('revIcon').style.backgroundColor = '#838ba3'; 
               
           
           }
           
         }
         
         $scope.showOdBook = function() {
          
           $scope.init();
           $scope.showOB=true;
           $scope.showPnL=false;
           $scope.showProjects=false;
           $scope.showAssociates=false;
             if($scope.showOB=true){
               document.getElementById('revIcon').style.backgroundColor = '#ffc20d'; 
               document.getElementById('pnlIcon').style.backgroundColor = '#838ba3'; 
               document.getElementById('projectIcon').style.backgroundColor = '#838ba3';
               
               }          
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

         var totalVal = $scope.totalOffShoreCount + $scope.totalOnShoreCount ;

       var sortedArray = [];
        for(var i in jsObj)
        {
            // Push each JSON Object entry in array by [value, key]
          //alert('----'+(jsObj[i]*100)/totalVal)
            //sortedArray.push([i,(jsObj[i]*100)/totalVal]);
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
