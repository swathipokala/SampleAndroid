package com.rrdonnelly.up2me.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	
	Date outputDate;
	String strOutputDate;
	public Date dateFormater(String oldFormat, String newFormat, String dateBaseDate) {

		    SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
		    SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
		    try {
		    	DateFormat formatter = new SimpleDateFormat(newFormat);
		    	outputDate = formatter.parse(sdf2.format(sdf1.parse(dateBaseDate)));
		    	
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
		
		return outputDate;
	}
	
	public String offersPageDateFormat(String oldFormat, String newFormat, String dateBaseDate) {

	    SimpleDateFormat sdf1 = new SimpleDateFormat(oldFormat);
	    SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
	    try {
	    	DateFormat formatter = new SimpleDateFormat(newFormat);
	    	strOutputDate = sdf2.format(sdf1.parse(dateBaseDate)).toString().trim();
	    	
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	
	return strOutputDate;
}
	
	public Date longDateFormater(){
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputDate;
	}

}
