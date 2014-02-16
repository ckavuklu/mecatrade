package com.meca.trade.to;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public final class TradeUtils {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
	
	
	public static final String convertStringDate(Date date) {
		
		String result = String.valueOf(date.getYear()+1900)
				+ StringUtils.leftPad(String.valueOf(date.getMonth() + 1), 2,
						'0')
				+ StringUtils.leftPad(String.valueOf(date.getDate()), 2, '0');
		return result;
	}

	public static final String convertStringTime(Date date) {
		String result = StringUtils.leftPad(String.valueOf(date.getHours()), 2,
				'0')
				+ StringUtils
						.leftPad(String.valueOf(date.getMinutes()), 2, '0')
				+ StringUtils
						.leftPad(String.valueOf(date.getSeconds()), 2, '0');
		return result;
	}

	
	public static Date getTime(String date) {
		Date dat = null;
		try {
			dat = dateFormat.parse(date + "000");
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		return dat;
	}
	
	
	public static Double roundDownDigits(Double param, Integer digitCount){
		BigDecimal value = new BigDecimal(param);
		BigDecimal roundOff = value.setScale(digitCount, BigDecimal.ROUND_DOWN);
		
		return roundOff.doubleValue();
	}
	
	
	public static Double roundUpDigits(Double param, Integer digitCount){
		BigDecimal value = new BigDecimal(param);
		BigDecimal roundOff = value.setScale(digitCount, BigDecimal.ROUND_HALF_UP);
		
		return roundOff.doubleValue();
	}

	public static Double getDouble(String value){
		return Double.valueOf(value);
	}
	
	public static Double getRoundedUpValue(Double val){
		
		return roundUpDigits(val,Constants.MARKET_LOT_PRECISION);
	}
}
