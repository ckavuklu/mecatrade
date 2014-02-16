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
	
	public final static Integer MARGIN_CALL_LEVEL = 20;
	
	public final static String ENDLN = "\r\n";
	
	public final static String FORMAT = ENDLN;
	
	public final static String SPACE = " ";
	
	public final static String SEPARATOR = ":";
	
	public final static Boolean DEBUG_ENABLED = false;
	
	public final static String GRAPH_DATA_JSON_START_STRING = "[";
	public final static String GRAPH_DATA_JSON_END_STRING = "]";
	public final static String GRAPH_DATA_JSON_SEPARATOR_STRING = ",";
	
	
	public final static String STOP_LOSS_VALUE_TYPE_EQUITY_PERCENTAGE = "equity-percentage";
	public final static String STOP_LOSS_VALUE_TYPE_POSITION_PERCENTAGE = "position-percentage";
	public final static String STOP_LOSS_VALUE_TYPE_POINT_TYPE = "point";
	
	public final static String STOP_LOSS_TYPE_POSITION_STOP_LOSS = "position-stop-loss";
	public final static String STOP_LOSS_TYPE_POSITION_TAKE_PROFIT = "position-take-profit";
	public final static String STOP_LOSS_TYPE_STRATEGY_STOP_LOSS = "strategy-stop-loss";
	
	
	public final static String GENERATION_DIRECTORY = "report";
	
	public final static String REPORT_GENERATION_DIRECTORY = GENERATION_DIRECTORY + File.separator +"generated";
	
	public final static String GRAPH_TEMPLATE_DIRECTORY = GENERATION_DIRECTORY + File.separator +"templates";
	
	public final static Integer MARKET_PRICE_PRECISION = 4;
	
	public final static Integer MARKET_LOT_PRECISION = 2;
	
	
}
