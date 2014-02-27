package com.meca.trade.to;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public final class TradeUtils {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
	
	public static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
	
	
	public static Date getQuarterEndDate(Date date){
		Calendar result = null;
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int year = cal.get(Calendar.YEAR);
	    
	    result = Calendar.getInstance();
		result.set(Calendar.YEAR,year);
		result.set(Calendar.DAY_OF_MONTH,1);
		result.set(Calendar.HOUR_OF_DAY,0);
		result.set(Calendar.MINUTE,0);
		result.set(Calendar.SECOND,0);
		result.set(Calendar.MILLISECOND,0);
		
		int quarter = (cal.get(Calendar.MONTH) / 3) + 1;
	    
		switch(quarter){
			case 1:{

				result.set(Calendar.MONTH,3);
			
				break;
			}
			case 2:{

				result.set(Calendar.MONTH,6);
				break;
			}
			case 3:{

				result.set(Calendar.MONTH,9);

				break;
			}
			case 4:{
				
				result.set(Calendar.YEAR,year+1);
				result.set(Calendar.MONTH,0);
				break;
			}
		}
		
		return result.getTime();
	}
	
	public static Date getQuarterStartDate(Date date){
		Calendar result = null;
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int year = cal.get(Calendar.YEAR);
	    
	    result = Calendar.getInstance();
		result.set(Calendar.YEAR,year);
		result.set(Calendar.DAY_OF_MONTH,1);
		result.set(Calendar.HOUR_OF_DAY,0);
		result.set(Calendar.MINUTE,0);
		result.set(Calendar.SECOND,0);
		result.set(Calendar.MILLISECOND,0);
		
		int quarter = (cal.get(Calendar.MONTH) / 3) + 1;
		
		switch(quarter){
			case 1:{
				
				
				result.set(Calendar.MONTH,0);
			
				break;
			}
			case 2:{
				
				result.set(Calendar.MONTH,3);
				break;
			}
			case 3:{
				
				result.set(Calendar.MONTH,6);
				break;
			}
			case 4:{
				
				result.set(Calendar.MONTH,9);
				break;
			}
		}
		
		return result.getTime();
	}
	
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
	
	public static String convertToString(Date date) {
		
		return date!=null?outputDateFormat.format(date):"null";
	}
	
	
	public static Double roundDownDigits(Double param, Integer digitCount){
		BigDecimal value = new BigDecimal(param);
		BigDecimal roundOff = value.setScale(digitCount, BigDecimal.ROUND_DOWN);
		
		return roundOff.doubleValue();
	}
	
	
	public static Double roundUpDigits(Double param, Integer digitCount) {

		if (param != null & param != Double.NaN) {
			BigDecimal value = new BigDecimal(param);
			BigDecimal roundOff = value.setScale(digitCount,
					BigDecimal.ROUND_HALF_UP);

			return roundOff.doubleValue();
		} else
			return null;
	}

	public static Double convertStringToDouble(String value,Integer precision){
		
		return roundUpDigits(Double.valueOf(value),precision);
	}
	
	public static Double getRoundedUpValue(Double val){
		
		return roundUpDigits(val,Constants.MARKET_LOT_PRECISION);
	}
}
