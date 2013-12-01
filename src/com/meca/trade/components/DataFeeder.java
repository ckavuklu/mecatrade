package com.meca.trade.components;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.MarketData;
import com.meca.trade.to.NullMarketData;
import com.meca.trade.to.SchedulingParameter;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")

@OutPort(value = "OUT", arrayPort = true)
@InPorts({
		@InPort(value = "SCHEDULETYPE", description = "type", type = String.class),
		@InPort(value = "SCHEDULEPERIOD", description = "period", type = Integer.class),
		@InPort(value = "PERIODINTERVAL", description = "period start-end date and time", type = String.class),
		@InPort(value = "SCHEDULEPERIOD", description = "period", type = Integer.class),
		@InPort(value = "TRADEDATA", description = "trade data", type = String.class) })
public class DataFeeder extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort scheduleTypePort, schedulePeriodPort, tradeDataPort, periodIntervalPort;

    OutputPort[] outportArray;

	private String schedule = null;
	
	private Integer schedulePeriod = null;
	private Integer previousTime = null;
	private Integer timeDividor = null;
	private Integer timeDecrement = null;
	private String placetoLook = null;
	
	private Double periodHigh;
	private Double periodLow;
	private Double periodOpen;
	private Double periodClose;
	
	private Date periodStart;
	private Date periodEnd;
	
	private Date cycleStart;
	private Date cycleEnd;
	
	
	private Date nextDate(Date date, Integer interval, String period){
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
	
	private Date getTime(String date, String time){
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(date.substring(0, 4).trim()), Integer.valueOf(date.substring(4, 6).trim())-1, Integer.valueOf(date.substring(6, 8).trim()), Integer.valueOf(time.substring(0, 2).trim()), Integer.valueOf(time.substring(2, 4).trim()), Integer.valueOf(time.substring(4, 6).trim()));
		return cal.getTime();
	}
	
	private String convertStringDate(Date date){
		String result = String.valueOf(date.getYear())+StringUtils.leftPad(String.valueOf(date.getMonth()+1),2,'0')+StringUtils.leftPad(String.valueOf(date.getDay()), 2, '0');
		return result;
	}
	
	private String convertStringTime(Date date){
		String result = StringUtils.leftPad(String.valueOf(date.getHours()),2,'0')+StringUtils.leftPad(String.valueOf(date.getMinutes()),2,'0')+StringUtils.leftPad(String.valueOf(date.getSeconds()), 2, '0');
		return result;
	}
	
	
	private void setPeriodIntervals(String interval){
		String[] periodTime = interval.split("-");
		String[] startTime = periodTime[0].split(":");
		String[] endTime = periodTime[1].split(":");
		
		periodStart = getTime(startTime[0],startTime[1]);
		periodEnd = getTime(endTime[0],endTime[1]);
	}

	private void clearIntervalParameters(){
		periodHigh = periodLow = periodOpen = periodClose = null;
	}

	private void setIntervalParameters(MarketData data) {
		Double high = Double.valueOf(data.getHigh());
		Double low = Double.valueOf(data.getLow());
		Double open = Double.valueOf(data.getOpen());
		Double close = Double.valueOf(data.getClose());
		
		
		if(periodHigh == null || (periodHigh < high)){
			periodHigh = high;
		}
		
		if(periodLow == null || (periodLow > low)){
			periodLow = low;
		}
		
		if(periodOpen == null){
			periodOpen = open;
		}
		
		periodClose = close;
		
	}
	
	@Override
	protected void execute() {

		Packet p = null;
		Packet schedulePer = null;
		Packet scheduleType = null;
		Packet periodIntervalPacket = null;
		String periodInterval = null;
		Boolean periodStarted = false;

		if (schedulePeriod == null) {
			schedulePer = schedulePeriodPort.receive();
			scheduleType = scheduleTypePort.receive();
			periodIntervalPacket = periodIntervalPort.receive();
			this.schedulePeriod = this.timeDecrement = new Integer(
					(Integer) schedulePer.getContent());
			this.schedule = new String((String) scheduleType.getContent());

			periodInterval = new String((String) periodIntervalPacket.getContent());
			
			drop(schedulePer);
			drop(scheduleType);
			drop(periodIntervalPacket);

			schedulePeriodPort.close();
			scheduleTypePort.close();
			periodIntervalPort.close();
		}
		
		
		
		setPeriodIntervals(periodInterval);
		
		cycleStart = periodStart;
		cycleEnd = nextDate(periodStart,schedulePeriod,schedule);
	
		while ((p = tradeDataPort.receive()) != null) {
			String[] trade = ((String) p.getContent()).split(",");

			MarketData data = new MarketData(
					new SchedulingParameter(1, "Minutes"));

			data.setQuote(trade[0]);
			data.setDate(trade[1]);
			data.setTime(trade[2]);
			data.setOpen(trade[3]);
			data.setHigh(trade[4]);
			data.setLow(trade[5]);
			data.setClose(trade[6]);
			data.setVolume(trade[7]);

			MarketData result = new NullMarketData();

			if (!schedule.equalsIgnoreCase("ALL")) {
				
				
				Date date = getTime(data.getDate(),data.getTime());
				
				System.out.println("DataFeeder.execute(): " + date);

				if(date.compareTo(cycleStart) >= 0){
					
					if(date.compareTo(cycleEnd) < 0){
						setIntervalParameters(data);
						
					}else if (date.compareTo(periodEnd) < 0) {
						
						if(periodHigh!=null && periodLow!=null){
							
							MarketData newData = new MarketData(
									new SchedulingParameter(schedulePeriod,
											schedule));
		
							newData.setQuote(trade[0]);
							newData.setDate(convertStringDate(cycleStart));
							newData.setTime(convertStringTime(cycleStart));
							newData.setOpen(String.valueOf(periodOpen));
							newData.setHigh(String.valueOf(periodHigh));
							newData.setLow(String.valueOf(periodLow));
							newData.setClose(String.valueOf(periodClose));
							newData.setVolume(trade[7]);

							result = newData;
							
							clearIntervalParameters();
							
							do{
								cycleStart = cycleEnd;
								cycleEnd = nextDate(cycleStart,schedulePeriod,schedule);
								
								// TODO : check whether required or not
								if(cycleEnd.compareTo(periodEnd) > 0) 
									{cycleEnd = periodEnd; break;} 
								
							}while(date.compareTo(cycleEnd) >= 0);
							
							
							setIntervalParameters(data);
						}
					}
				}else{
					
				}
				
				
			} else {
				result = data;
			}

			try {

				if (!(result instanceof NullMarketData)) {

					for (int i = 0; i < outportArray.length; i++) {

						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(result));
							// System.out.println("DataFeeder sent " +
							// result.toString());
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				drop(p);
			}

		}

		tradeDataPort.close();

	}


	@Override
	protected void openPorts() {

		scheduleTypePort = openInput("SCHEDULETYPE");
		schedulePeriodPort = openInput("SCHEDULEPERIOD");
		tradeDataPort = openInput("TRADEDATA");
		periodIntervalPort = openInput("PERIODINTERVAL");

		outportArray = openOutputArray("OUT");

	}
}
