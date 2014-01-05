package com.meca.trade.components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.TradeUtils;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")
@OutPort(value = "OUT", arrayPort = true)
@InPorts({
	@InPort(value = "KICKOFF", description = "type", type = Double.class),
	@InPort(value = "CLOCKTICK", description = "type", type = Double.class),
		@InPort(value = "FILENAME", description = "FileName", type = String.class),
		@InPort(value = "SCHEDULETYPE", description = "type", type = String.class),
		@InPort(value = "SCHEDULEPERIOD", description = "period", type = Integer.class),
		@InPort(value = "PERIODSTART", description = "period start", type = Date.class),
		@InPort(value = "PERIODEND", description = "period end date and time", type = Date.class),
		@InPort(value = "SCHEDULEPERIOD", description = "period", type = Integer.class) })
public class DataFeeder extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort scheduleTypePort, schedulePeriodPort, tradeDataPort,
			periodStartPort,periodEndPort, kickoffPort, clockTickPort;

	OutputPort[] outportArray;
	InputPort fileName;

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

	@Override
	protected void execute() {

		Packet p = null;
		Packet schedulePer = null;
		Packet scheduleType = null;
		Packet periodStartPacket = null;
		Packet periodEndPacket = null;
		Boolean periodStarted = false;
		Packet kickoffPacket = null;
		boolean kickOff = false;
		Packet c = null;


		Packet ctp = null;

		if (schedulePeriod == null) {
			schedulePer = schedulePeriodPort.receive();
			scheduleType = scheduleTypePort.receive();
			periodStartPacket = periodStartPort.receive();
			periodEndPacket = periodEndPort.receive();
			ctp = fileName.receive();

			this.schedulePeriod = this.timeDecrement = new Integer(
					(Integer) schedulePer.getContent());
			this.schedule = new String((String) scheduleType.getContent());

			periodStart = (Date) periodStartPacket.getContent();
			periodEnd = (Date) periodEndPacket.getContent();
			
			drop(schedulePer);
			drop(scheduleType);
			drop(periodStartPacket);
			drop(periodEndPacket);

			schedulePeriodPort.close();
			scheduleTypePort.close();
			periodStartPort.close();
			periodEndPort.close();
			fileName.close();
		}
		
		if (kickoffPacket == null) {
			kickoffPacket = kickoffPort.receive();
			kickOff = true;
			kickoffPort.close();
			drop(kickoffPacket);
		}

		
		
		cycleStart = periodStart;
		cycleEnd = nextDate(periodStart, schedulePeriod, schedule);

		String cti = (String) ctp.getContent();
		cti = cti.trim();

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(cti));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			drop(ctp);
		}

		String line = null;
		try {
			line = br.readLine();

			while ((line = br.readLine()) != null && kickOff) {

				String[] trade = line.split(",");

				PriceData data = null;
				PriceData result = null;

				if (!schedule.equalsIgnoreCase("ALL")) {

					Date date = TradeUtils.getTime(trade[1] + "-" + trade[2]);
					
					if (cycleEnd.compareTo(periodEnd) > 0) {

						// Exit as we reach period end
						PriceData dataEnd = new PriceData(-1d,-1d,-1d,-1d);

						dataEnd.setQuote(trade[0]);
						dataEnd.setTime(cycleStart);
						dataEnd.setVolume(TradeUtils.getDouble(trade[7]));

						result = dataEnd;
						
					}
					

					else if (date.compareTo(cycleStart) >= 0) {

						if (date.compareTo(cycleEnd) < 0) {
							setIntervalParameters(trade[4],trade[5],trade[3],trade[6]);

						} else {

							if (periodHigh != null && periodLow != null) {

								PriceData newData = new PriceData(periodOpen,periodClose,periodHigh,periodLow);

								newData.setQuote(trade[0]);
								newData.setTime(cycleStart);
								newData.setVolume(TradeUtils.getDouble(trade[7]));
								
								result = newData;
								
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
					data = new PriceData(TradeUtils.getDouble(trade[3]),TradeUtils.getDouble(trade[6]),TradeUtils.getDouble(trade[4]),TradeUtils.getDouble(trade[5]));
					Date date = TradeUtils.getTime(trade[1] + "-" + trade[2]);
					data.setQuote(trade[0]);	
					data.setTime(date);
					data.setVolume(TradeUtils.getDouble(trade[7]));
					result = data;
				}

				try {

					if (result != null) {
						if(Constants.DEBUG_ENABLED)
							System.out.println("DataFeeder-2: " + result);
						
						
						
						for (int i = 0; i < outportArray.length; i++) {
							
							if (outportArray[i].isConnected()) {
								outportArray[i].send(create(result));
							}
						}
						

						//This is to wait for the clock tick
						c = clockTickPort.receive();
						drop(c);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					if (result != null
							&& Double.valueOf(result.getHigh()) < 0) {
						break;
					}
				}

			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			clockTickPort.close();
		}

	}

	@Override
	protected void openPorts() {

		scheduleTypePort = openInput("SCHEDULETYPE");
		schedulePeriodPort = openInput("SCHEDULEPERIOD");
		fileName = openInput("FILENAME");
		periodStartPort = openInput("PERIODSTART");
		periodEndPort = openInput("PERIODEND");
		clockTickPort = openInput("CLOCKTICK");
		kickoffPort = openInput("KICKOFF");

		outportArray = openOutputArray("OUT");

	}
}
