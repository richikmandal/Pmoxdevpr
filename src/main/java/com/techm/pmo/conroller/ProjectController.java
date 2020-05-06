package com.techm.pmo.conroller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.techm.pmo.model.Casum;
import com.techm.pmo.model.ProfitAndLossData;
import com.techm.pmo.model.User;
import com.techm.pmo.service.PmrService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {


  @Autowired
  private PmrService      pmrservice;


  private final Logger    log = LoggerFactory.getLogger(ProjectController.class);



  @PostMapping("/getprojectsfruser")
  public String getUser(@RequestBody User user) {

    Gson gsonRes = new Gson();
    log.debug("REST request to get User : {}", user);

    return gsonRes.toJson(pmrservice.getPmrDataFrUser(user));// ResponseEntity<>(userService.getUserWithRoleByUsername(username),
                                                             // HttpStatus.OK);
  }
  

  @GetMapping("/getPmrSmryDataFrUser/{username}")
  public String getPmrSmryDataFrUser(@PathVariable String username) {

    Gson gsonRes = new Gson();

    return gsonRes.toJson(pmrservice.getPmrSmryDataFrUser(username));// ResponseEntity<>(userService.getUserWithRoleByUsername(username),
                                                                     // HttpStatus.OK);
  }
  
  @GetMapping("/getPrjMasterDataFrUser/{username}")
  public String getPrjMasterDataFrUser(@PathVariable String username) {

    Gson gsonRes = new Gson();

    return gsonRes.toJson(pmrservice.getPrjMasterDataFrUser(username));// ResponseEntity<>(userService.getUserWithRoleByUsername(username),
                                                                     // HttpStatus.OK);
  }

  @GetMapping("/getprofitandloss/{user}")
  public List<ProfitAndLossData> getP_L(@PathVariable String user) {
    System.out.println("richik" + user);
    return pmrservice.getP_L(user);
  }
  
  @GetMapping("/getCasum/{user}")
  public List<Casum> getCasum(@PathVariable String user) {
    System.out.println("Subrajit" + user);
    return pmrservice.getCasum(user);
  }
  
  @PostMapping("/getPmSeriesData")
  public String getPmSeriesData(@RequestBody User user) {

    Gson gsonRes = new Gson();
    log.debug("REST request to get User : {}", user);

    return gsonRes.toJson(pmrservice.getPmSeriesData(user));// ResponseEntity<>(userService.getUserWithRoleByUsername(username),
                                                             // HttpStatus.OK);
  }


}
