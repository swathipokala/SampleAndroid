package com.rrdonnelly.up2me.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberFormatUtil {
	
	public static String insertCommas(BigDecimal number) {
  	  // for your case use this pattern -> #,##0.00
  	DecimalFormat df = new DecimalFormat("#,##0.00");
  	return df.format(number);
  }

  public static String insertCommas(String number) {
  	return insertCommas(new BigDecimal(number));
  }

}
