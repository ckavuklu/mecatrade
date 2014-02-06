package com.meca.trade.to;

import java.io.File;
import java.math.BigDecimal;

public final class Constants {

	public final Double END_OF_MARKET_VALUE = -999d;
	
	public final static Double EURUSD_SPREAD = 0.0002d;
	
	public final static Double SAFETY_FACTOR = 1.5d;
	
	public final static Double SAFETY_MARGIN_PERCENTAGE = 0.1d;
	
	public final static Integer EURUSD_LOT_SIZE = 100000;
	
	public final static Integer EURUSD_LEVERAGE = 100;
	
	public final static Integer USDTRY_LOT_SIZE = 100000;
	
	public final static Integer USDTRY_LEVERAGE = 100;
	
	public final static String ENDLN = "\r\n";
	
	public final static String FORMAT = ENDLN;
	
	public final static String SPACE = " ";
	
	public final static String SEPARATOR = ":";
	
	public final static Boolean DEBUG_ENABLED = true;
	
	public final static String GRAPH_DATA_JSON_START_STRING = "[";
	public final static String GRAPH_DATA_JSON_END_STRING = "]";
	public final static String GRAPH_DATA_JSON_SEPARATOR_STRING = ",";
	
	public final static String GENERATION_DIRECTORY = "report";
	
	public final static String REPORT_GENERATION_DIRECTORY = GENERATION_DIRECTORY + File.separator +"generated";
	
	public final static String GRAPH_TEMPLATE_DIRECTORY = GENERATION_DIRECTORY + File.separator +"templates";
	
	public static Double getRoundedUpValue(Double val){
		BigDecimal result = new BigDecimal(val);
		
		return result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
