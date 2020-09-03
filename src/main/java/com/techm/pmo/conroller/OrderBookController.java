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
import com.techm.pmo.model.CasumData;
import com.techm.pmo.model.Pmrdata;
import com.techm.pmo.model.ProfitAndLossData;
import com.techm.pmo.model.User;
import com.techm.pmo.service.PmrService;

@RestController
@RequestMapping("/api/ob")
public class OrderBookController {


  @Autowired
  private PmrService    pmrservice;


  private final Logger    log = LoggerFactory.getLogger(OrderBookController.class);



  @PostMapping("/getPos")
  public String getPoReceived(@RequestBody User user) {

    Gson gsonRes = new Gson();
    log.debug("REST request to get User : {}", user);
    
    List<CasumData> poList = pmrservice.getPoReceived(user);

    return gsonRes.toJson(poList);// ResponseEntity<>(userService.getUserWithRoleByUsername(username),
                                                             // HttpStatus.OK);
  }

}
