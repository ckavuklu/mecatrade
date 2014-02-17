package com.meca.trade.to;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.meca.trade.networks.Parameter;

public class MarketDataGenerator {
	String sourceFileName;
	//String targetFileName;

	private Double periodHigh;
	private Double periodLow;
	private Double periodOpen;
	private Double periodClose;

	private Date periodStart;
	private Date periodEnd;

	private Date cycleStart;
	private Date cycleEnd;
	
	private Integer schedulePeriod = null;
	private String schedule = null;
	private List<PriceData> marketData = null;
	MarketType marketType = null;
	
	public Iterator<PriceData> getMarketDataIterator(){
		return marketData.iterator();
	}
	
	public MarketDataGenerator(HashMap<String, Parameter> parameterMap) throws IOException {
		//targetFileName = (String)parameterMap.get("INPUT_MARKET_DATA_FILE_NAME").getValue();
		sourceFileName = "ORG_" + (String)parameterMap.get("INPUT_MARKET_DATA_FILE_NAME").getValue();
		periodStart = (Date)parameterMap.get("PERIOD_START").getValue();
		periodEnd = (Date)parameterMap.get("PERIOD_END").getValue();
		schedulePeriod = (Integer)parameterMap.get("PERIOD_STEP_SIZE").getValue();
		schedule = (String)parameterMap.get("PERIOD_TYPE").getValue();
		marketData = new ArrayList<PriceData>();
		cycleStart 	= periodStart;
		cycleEnd 	= nextDate(periodStart, schedulePeriod, schedule);
		
		
		BufferedReader br = null;
		//PrintWriter wr = null;

		try {
			br = new BufferedReader(new FileReader(sourceFileName));
			//wr = new PrintWriter(new FileWriter(targetFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			
		}
		

		String cycleMarketData;	
		String line = null;
		try {
			line = br.readLine();

			while ((line = br.readLine()) != null) {

				
				String[] trade = line.split(",");

				boolean endOfData = false;
				boolean result = false;
				
				
				if(trade[0].equalsIgnoreCase(MarketType.EURUSD.toString())){
					marketType = MarketType.EURUSD;
				} else if(trade[0].equalsIgnoreCase(MarketType.USDTRY.toString())){
					marketType = MarketType.USDTRY;
				}

				if (!schedule.equalsIgnoreCase("ALL")) {

					Date date = TradeUtils.getTime(trade[1] + "-" + trade[2]);
					
					if (cycleEnd.compareTo(periodEnd) > 0) {
						endOfData = true;
						result = true;
						
						
						/*PriceData data = new PriceData();
						data.setTime(cycleStart);
						data.setVolume(TradeUtils.getDouble(trade[7]));
						data.setClose(-1d);
						data.setOpen(-1d);
						data.setHigh(-1d);
						data.setLow(-1d);
						marketData.add(data);*/
						
					}
					

					else if (date.compareTo(cycleStart) >= 0) {

						if (date.compareTo(cycleEnd) < 0) {
							setIntervalParameters(trade[4],trade[5],trade[3],trade[6]);

						} else {

							if (periodHigh != null && periodLow != null) {

								result = true;
								
								PriceData data = new PriceData(marketType);
								data.setTime(cycleStart);
								data.setVolume(TradeUtils.convertStringToDouble(trade[7],marketType.getPricePrecision()));
								data.setClose(periodClose);
								data.setOpen(periodOpen);
								data.setHigh(periodHigh);
								data.setLow(periodLow);
								marketData.add(data);
								
								clearIntervalParameters();
								
							} 
							

							do {
								cycleStart = cycleEnd;
								cycleEnd = nextDate(cycleStart,
										schedulePeriod, schedule);

							} while (date.compareTo(cycleEnd) >= 0);
							
							setIntervalParameters(trade[4],trade[5],trade[3],trade[6]);
						}
					} else {
						
					}

				} else {
					result = true;
					
					PriceData data = new PriceData(TradeUtils.convertStringToDouble(trade[3],marketType.getPricePrecision()),TradeUtils.convertStringToDouble(trade[6],marketType.getPricePrecision()),TradeUtils.convertStringToDouble(trade[4],marketType.getPricePrecision()),TradeUtils.convertStringToDouble(trade[5],marketType.getPricePrecision()),marketType);
					Date date = TradeUtils.getTime(trade[1] + "-" + trade[2]);
					data.setQuote(trade[0]);	
					data.setTime(date);
					data.setVolume(TradeUtils.convertStringToDouble(trade[7],marketType.getPricePrecision()));
					
					marketData.add(data);
					
				}

				if (result && endOfData) {
					break;
				}

			}


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
			//wr.close();
		}
		
		
	}
	

	private void clearIntervalParameters() {
		periodHigh = periodLow = periodOpen = periodClose = null;
	}

	
	
	private void setIntervalParameters(String highParam, String lowParam, String openParam, String closeParam) {
		Double high = TradeUtils.convertStringToDouble(highParam,marketType.getPricePrecision());
		Double low = TradeUtils.convertStringToDouble(lowParam,marketType.getPricePrecision());
		Double open = TradeUtils.convertStringToDouble(openParam,marketType.getPricePrecision());
		Double close = TradeUtils.convertStringToDouble(closeParam,marketType.getPricePrecision());

		if (periodHigh == null || (periodHigh < high)) {
			periodHigh = high;
		}

		if (periodLow == null || (periodLow > low)) {
			periodLow = low;
		}

		if (periodOpen == null) {
			periodOpen = open;
		}

		periodClose = close;

	}

	
	private Date nextDate(Date date, Integer interval, String period) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		if (period.equalsIgnoreCase("Minute")) {

			cal.add(Calendar.MINUTE, interval);
			
		} else if (period.equalsIgnoreCase("Hour")) {
			cal.add(Calendar.HOUR_OF_DAY, interval);

		} else if (period.equalsIgnoreCase("Day")) {
			cal.add(Calendar.DATE, interval);

		} else if (period.equalsIgnoreCase("Week")) {
			cal.add(Calendar.WEEK_OF_YEAR, interval);

		} else if (period.equalsIgnoreCase("Month")) {
			cal.add(Calendar.MONTH, interval);

		} else if (period.equalsIgnoreCase("Year")) {
			cal.add(Calendar.YEAR, interval);
		}

		return cal.getTime();
	}

}
