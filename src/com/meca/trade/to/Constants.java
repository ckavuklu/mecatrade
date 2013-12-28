package com.meca.trade.to;

import java.math.BigDecimal;

public final class Constants {

	public final Double END_OF_MARKET_VALUE = -999d;
	
	public final static Double EURUSD_SPREAD = 0.0002d;
	
	public final static Integer EURUSD_LOT_SIZE = 100000;
	
	public final static Integer EURUSD_LEVERAGE = 100;
	
	public final static Integer USDTRY_LOT_SIZE = 100000;
	
	public final static Integer USDTRY_LEVERAGE = 100;
	
	public final static String ENDLN = "\r\n";
	
	public final static String FORMAT = ENDLN;
	
	public final static String SPACE = " ";
	
	public final static String SEPARATOR = ":";
	
	public static Double getRoundedUpValue(Double val){
		BigDecimal result = new BigDecimal(val);
		
		return result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
