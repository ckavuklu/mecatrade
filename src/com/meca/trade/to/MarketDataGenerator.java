package com.meca.trade.to;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.meca.trade.networks.Parameter;

public class MarketDataGenerator {
	String sourceFileName;
	String targetFileName;

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
	
	public MarketDataGenerator(HashMap<String, Parameter> parameterMap) throws IOException {
		targetFileName = (String)parameterMap.get("INPUT_MARKET_DATA_FILE_NAME").getValue();
		sourceFileName = "ORG_" + targetFileName;
		periodStart = (Date)parameterMap.get("PERIOD_START").getValue();
		periodEnd = (Date)parameterMap.get("PERIOD_END").getValue();
		schedulePeriod = (Integer)parameterMap.get("PERIOD_STEP_SIZE").getValue();
		schedule = (String)parameterMap.get("PERIOD_TYPE").getValue();
		
		cycleStart = periodStart;
		cycleEnd = nextDate(periodStart, schedulePeriod, schedule);
		
		
		BufferedReader br = null;
		PrintWriter wr = null;

		try {
			br = new BufferedReader(new FileReader(sourceFileName));
			wr = new PrintWriter(new FileWriter(targetFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			
		}

		String line = null;
		try {
			line = br.readLine();

			while ((line = br.readLine()) != null) {

				String[] trade = line.split(",");

				boolean data = false;
				boolean result = false;

				if (!schedule.equalsIgnoreCase("ALL")) {

					Date date = TradeUtils.getTime(trade[1] + "-" + trade[2]);
					
					if (cycleEnd.compareTo(periodEnd) > 0) {
						data = true;
						result = true;
					}
					

					else if (date.compareTo(cycleStart) >= 0) {

						if (date.compareTo(cycleEnd) < 0) {
							setIntervalParameters(trade[4],trade[5],trade[3],trade[6]);

						} else {

							if (periodHigh != null && periodLow != null) {

								result = true;
								
								clearIntervalParameters();
								
								do {
									cycleStart = cycleEnd;
									cycleEnd = nextDate(cycleStart,
											schedulePeriod, schedule);

								} while (date.compareTo(cycleEnd) >= 0);
								
								setIntervalParameters(trade[4],trade[5],trade[3],trade[6]);
								
							} 
						}
					} else {

					}

				} else {
					result = true;
				}

				try {

					if (result) {
						if(Constants.DEBUG_ENABLED)
							System.out.println("DataFeeder-2: " + result);
						
						//SEND
						wr.println(line);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
					if (result && data) {
						break;
					}

				}

			}


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
			wr.close();
		}
		
		
	}
	

	private void clearIntervalParameters() {
		periodHigh = periodLow = periodOpen = periodClose = null;
	}

	
	
	private void setIntervalParameters(String highParam, String lowParam, String openParam, String closeParam) {
		Double high = Double.valueOf(highParam);
		Double low = Double.valueOf(lowParam);
		Double open = Double.valueOf(openParam);
		Double close = Double.valueOf(closeParam);

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
			cal.add(Calendar.HOUR, interval);

		} else if (period.equalsIgnoreCase("Day")) {
			cal.add(Calendar.DATE, interval);

		} else if (period.equalsIgnoreCase("Month")) {
			cal.add(Calendar.MONTH, interval);

		} else if (period.equalsIgnoreCase("Year")) {
			cal.add(Calendar.YEAR, interval);
		}

		return cal.getTime();
	}

}
