package com.techm.pmo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class PmoxUtil {

	private static final Logger log = LoggerFactory.getLogger(PmoxUtil.class);

	private static final String APPLICATION_NAME = "osdma";

	private PmoxUtil() {
	}

	public static String convertMillion(String param) {
	  
	  float milConv = 0;
	  
	  if(param!=null) {
	  
		milConv = Float.parseFloat(param)*1000000;
		
	  }
		
		return milConv+"";
	}
	
}
