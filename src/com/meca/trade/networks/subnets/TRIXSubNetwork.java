package com.meca.trade.networks.subnets;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.SubNet;
import com.meca.trade.to.TradeData;

@OutPorts({
		@OutPort(value = "TRIX", description = "Triple Smoothed EMA", type = Double.class),
		@OutPort(value = "SIGNALLINE", description = "Signal Line", type = Double.class)})

@InPort(value = "INPUT", description = "Trade Input", type = Double.class)


public class TRIXSubNetwork extends SubNet {

	@Override
	protected void define() {

		component("SUBIN", com.jpmorrsn.fbp.engine.SubInComponent.class);
		component("SUBOUT", com.jpmorrsn.fbp.engine.SubOutComponent.class);
	//	component("POPULATE", com.meca.trade.components.PopulateInput.class);
		component("1st_EMA", com.meca.trade.components.ExponentialMovingAverage.class);
		component("2nd_EMA", com.meca.trade.components.ExponentialMovingAverage.class);
		component("3rd_EMA", com.meca.trade.components.ExponentialMovingAverage.class);
		component("9P_EMA", com.meca.trade.components.ExponentialMovingAverage.class);
		component("1P_PERCHNG", com.meca.trade.components.OnePeriodPercentageChange.class);
	
		initialize("INPUT", component("SUBIN"), port("NAME", 0));

		

		connect(component("SUBIN"), port("OUT", 0), component("1st_EMA"),
				port("DATA"));
		
		connect(component("1st_EMA"), port("OUT", 0), component("2nd_EMA"),
				port("DATA"));

		connect(component("2nd_EMA"), port("OUT", 0), component("3rd_EMA"),
				port("DATA"));

		connect(component("3rd_EMA"), port("OUT", 0), component("1P_PERCHNG"),
				port("INPUT"));
		
		connect(component("1P_PERCHNG"), port("OUT", 1), component("9P_EMA"),
				port("DATA"));
		
		// SubNet Out
		connect(component("1P_PERCHNG"), port("OUT", 0), component("SUBOUT"),
				port("IN", 0));
		
		connect(component("9P_EMA"), port("OUT", 0), component("SUBOUT"),
				port("IN", 1));
		
		
		// Initialize with constanst - To Be Inputted 
		
		initialize(Integer.valueOf(15), component("1st_EMA"), port("WINDOW"));
		initialize(Integer.valueOf(15), component("2nd_EMA"), port("WINDOW"));
		initialize(Integer.valueOf(15), component("3rd_EMA"), port("WINDOW"));
		initialize(Integer.valueOf(9), component("9P_EMA"), port("WINDOW"));

		
		initialize("TRIX", component("SUBOUT"), port("NAME", 0));
		initialize("SIGNALLINE", component("SUBOUT"), port("NAME", 1));

	}

}
