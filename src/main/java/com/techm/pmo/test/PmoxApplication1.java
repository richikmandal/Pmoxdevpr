package com.techm.pmo.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PmoxApplication1 {

	public static void main(String[] args) throws ParseException {
		
	  
	  String end_time = "1983-12-19 22:20:18";
      //start_time1 = strArray1[1];
      System.out.println(end_time);
      //rs.close();
      SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:mm:SS");
      Date d1 = null;
      Date d2 = null;
      d1 = format.parse(end_time);
      d2 = format.parse(end_time);
      System.out.println(d1);
      long diff1 = d2.getTime() - d1.getTime();
      long diffMinutes1 = diff1 / (60 * 1000) % 60;
      long diffhrs1 = diff1 / (60 * 60 * 1000) % 24;
      long difference = diffhrs1 * 60 + diffMinutes1;
	  
	  
      String sDate1="1998/12/13 22:20:18";  
      Date date1=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(sDate1);  
      System.out.println(sDate1+"\t"+date1); 
	  
	}

}
