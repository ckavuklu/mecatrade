package com.meca.trade.to;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TradeUtils {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
	
	public static Date getTime(String date) {
		Date dat = null;
		try {
			dat = dateFormat.parse(date + "000");
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		return dat;
	}
	
	
	public static Double roundToTwoDigits(Double lot){
		//System.out.println("BaseTrader.evaluateProperLot() before:" + lot);
		BigDecimal value = new BigDecimal(lot);
		BigDecimal roundOff = value.setScale(2, BigDecimal.ROUND_DOWN);
		
		//System.out.println("BaseTrader.evaluateProperLot() after:" + roundOff.doubleValue());
		return roundOff.doubleValue();
	}

}
