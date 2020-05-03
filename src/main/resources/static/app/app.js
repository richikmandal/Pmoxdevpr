// Creating angular JWTDemoApp with module name "JWTDemoApp"
angular.module("PmoxApp", ['ui.router','ngRoute','angularUtils.directives.dirPagination'])
// the following method will run at the time of initializing the module. That
// means it will run only one time.
  .directive('hcChart', function () {
                return {
                    restrict: 'E',
                    template: '<div></div>',
                    scope: {
                        options: '='
                    },
                    link: function (scope, element) {

                      Highcharts.chart(element[0], scope.options);

                      scope.$watch('options', function(newVal) {
                        if (newVal) {
                        Highcharts.chart(element[0], scope.options);
                        }
                      }, true);
                    }
                };
            })
            // Directive for pie charts, pass in title and data only    
            .directive('hcPieChart', function () {
                return {
                    restrict: 'E',
                    template: '<div></div>',
                    scope: {
                        title: '@',
                        data: '='
                    },
                    link: function (scope, element) {
                     Highcharts.chart(element[0], {
                            chart: {
                                type: 'pie',options3d: {
                                  enabled: true,
                                  alpha: 45,
                                  beta: 0
                              }
                            },
                            credits: {
                              enabled: false
                            },
                            title: {
                                text: scope.title
                            },
                            plotOptions: {
                                pie: {
                                  innerSize: 100,
                                  depth: 40,
                                    allowPointSelect: true,
                                    cursor: 'pointer',
                                    dataLabels: {
                                        enabled: true,
                                        format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                                    }
                                }
                            },
                            series: [{
                                data: scope.data
                            }]
                        });
                        
                        scope.$watch('data', function(newVal) {
                          if (newVal) {
                            Highcharts.chart(element[0], {
                              chart: {
                                  type: 'pie',options3d: {
                                    enabled: true,
                                    alpha: 45,
                                    beta: 0
                                }
                              },
                              credits: {
                                enabled: false
                              },
                              title: {
                                  text: scope.title
                              },
                              plotOptions: {
                                  pie: {
                                    depth: 40,
                                      allowPointSelect: true,
                                      cursor: 'pointer',
                                      dataLabels: {
                                          enabled: true,
                                          format: '<b>{point.name}</b>: {point.y}'
                                      }
                                  }
                              },
                              series: [{
                                  data: scope.data
                              }]
                          });
                            
                          }
                        }, true);
                    }
                };
            }).run(function (AuthService, $rootScope, $state) {
        // For implementing the authentication with ui-router we need to listen the
        // state change. For every state change the ui-router module will broadcast
        // the '$stateChangeStart'.
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
            // checking the user is logged in or not
          if (!AuthService.user) {
                // To avoiding the infinite looping of state change we have to add a
                // if condition.
                if (toState.name != 'login' && toState.name != 'register') {
                    event.preventDefault();
                    $state.go('login');
                }
            } else {
                // checking the user is authorized to view the states
                if (toState.data && toState.data.role) {
                    var hasAccess = false;
                    alert(AuthService.user.roleName)
                    var role = AuthService.user.roleName.split(",") ;
                    alert(role)
                    for (var i = 0; i < role.length; i++) {
                        var role = AuthService.user.role[i];
                        if (toState.data.role == role) {
                            hasAccess = true;
                            break;
                        }
                    }
                    if (!hasAccess) {
                        event.preventDefault();
                        $state.go('access-denied');
                    }

                }
            }
        });
    });