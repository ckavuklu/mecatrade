package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.NullTradeData;
import com.meca.trade.to.SchedulingParameter;
import com.meca.trade.to.TradeData;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")

@OutPort(value = "OUT", arrayPort = true)
@InPorts({
		@InPort(value = "SCHEDULETYPE", description = "type", type = String.class),
		@InPort(value = "SCHEDULEPERIOD", description = "period", type = Integer.class),
		@InPort(value = "TRADEDATA", description = "trade data", type = String.class) })
public class DataFeeder extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort scheduleTypePort, schedulePeriodPort, tradeDataPort;

    OutputPort[] outportArray;

	private String schedule = null;
	private Integer schedulePeriod = null;
	private Integer previousTime = null;
	private Integer timeDividor = null;
	private Integer timeDecrement = null;
	private String placetoLook = null;

	@Override
	protected void execute() {

		Packet p = null;
		Packet schedulePer = null;
		Packet scheduleType = null;

		if (schedulePeriod == null) {
			schedulePer = schedulePeriodPort.receive();
			scheduleType = scheduleTypePort.receive();
			this.schedulePeriod = this.timeDecrement = new Integer((Integer) schedulePer
					.getContent());
			this.schedule = new String ((String) scheduleType.getContent());
			
			drop(schedulePer);
			drop(scheduleType);
			
			schedulePeriodPort.close();
			scheduleTypePort.close();
		}

		while ((p = tradeDataPort.receive()) != null) {
			String[] trade = ((String) p.getContent()).split(",");

			TradeData data = new TradeData(
					new SchedulingParameter(1, "Minutes"));

			data.setQuote(trade[0]);
			data.setDate(trade[1]);
			data.setTime(trade[2]);
			data.setOpen(trade[3]);
			data.setHigh(trade[4]);
			data.setLow(trade[5]);
			data.setClose(trade[6]);
			data.setVolume(trade[7]);

			TradeData result = new NullTradeData();

			if (schedule.equalsIgnoreCase("Minute")) {
				placetoLook = data.getTime().substring(2, 4);
				timeDividor = 60;

			} else if (schedule.equalsIgnoreCase("Hour")) {
				placetoLook = data.getTime().substring(0, 2);
				timeDividor = 24;

			} else if (schedule.equalsIgnoreCase("Day")) {
				placetoLook = data.getDate().substring(6, 8);

				if (data.getTime().equalsIgnoreCase("030000")) {
					timeDecrement--;
				}

			} else if (schedule.equalsIgnoreCase("Month")) {
				placetoLook = data.getDate().substring(4, 6);
				timeDividor = 12;

			} else if (schedule.equalsIgnoreCase("Year")) {
				placetoLook = data.getDate().substring(0, 4);
				timeDividor = 9999;
			}

			Integer time = Integer.valueOf(placetoLook);

			if (previousTime == null) {
				previousTime = time;
			}

			if (timeDividor != null) {
				if (((previousTime + schedulePeriod) % timeDividor) == time) {
					previousTime = time;
					
					data.setParam(new SchedulingParameter(
							schedulePeriod, schedule));
					
					result = data;
					
				}
			} else {
				if (timeDecrement == 0) {
					data.setParam(new SchedulingParameter(
							schedulePeriod, schedule));
					
					result = data;
					this.timeDecrement = schedulePeriod;
				}
			}

			try {

				if (!(result instanceof NullTradeData)) {
					
					for(int i=0;i<outportArray.length;i++){
					
						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(result));
							//System.out.println("DataFeeder sent " + result.toString());
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

		outportArray = openOutputArray("OUT");

	}
}
