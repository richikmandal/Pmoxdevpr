package com.techm.pmo.conroller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techm.pmo.model.User;
import com.techm.pmo.service.UserService;
import com.techm.pmo.service.UtilityService;

@RestController
@RequestMapping("/api/utility")
public class UtilityController {

	private final Logger log = LoggerFactory.getLogger(UserController.class);

	@Resource
	private UtilityService utilityService;

	@GetMapping("/getCdmNames")
	public ResponseEntity<List<String>> getCdmNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getCdmNames(), HttpStatus.OK);
	}

	@GetMapping("/getBrmNames")
	public ResponseEntity<List<String>> getBrmNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getBrmNames(), HttpStatus.OK);
	}

	@GetMapping("/getIbuHeadNames")
	public ResponseEntity<List<String>> getIbuHeadNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getIbuHeadNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getIbuSNames")
	public ResponseEntity<List<String>> getIbuSNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getIbuSNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getBizNames")
	public ResponseEntity<List<String>> getBizNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getBizNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getSbuBizNames")
	public ResponseEntity<List<String>> getSbuBizNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getSbuBizNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getCompetencyNames")
	public ResponseEntity<List<String>> getCompetencyNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getCompetencyNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getProjAnnNames")
	public ResponseEntity<List<String>> getProjAnnNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getProjAnnNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getHwTwNames")
	public ResponseEntity<List<String>> getHwTwNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getHwTwNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getGeNongeNames")
	public ResponseEntity<List<String>> getGeNongeNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getGeNongeNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getVerticalNames")
	public ResponseEntity<List<String>> getVerticalNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getVerticalNames(), HttpStatus.OK);
	}
	
	@GetMapping("/getDysonNames")
	public ResponseEntity<List<String>> getDysonNames() {
		log.debug("REST request to get User : {}");

		return new ResponseEntity<List<String>>(utilityService.getDysonNames(), HttpStatus.OK);
	}
}
