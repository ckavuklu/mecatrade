package com.meca.trade.networks.subnets;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.SubNet;
import com.meca.trade.to.TradeData;

@OutPorts({
		@OutPort(value = "MACDLINE", description = "MACD Line"),
		@OutPort(value = "SIGNALLINE", description = "Signal Line"),
		@OutPort(value = "HISTOGRAM", description = "MACD Histogram Line") })
@InPort(value = "INPUT", description = "Trade Input", type = Double.class)
public class MACDSubNetwork extends SubNet {

	@Override
	protected void define() {

		component("SUBIN", com.jpmorrsn.fbp.engine.SubInComponent.class);
		component("SUBOUT", com.jpmorrsn.fbp.engine.SubOutComponent.class);
		component("POPULATE", com.meca.trade.components.PopulateInput.class);
		/*component("SUBTRACT", com.meca.trade.components.Calculator.class);
		component("SUBTRACTHISTOGRAM",
				com.meca.trade.components.Calculator.class);*/

		initialize("INPUT", component("SUBIN"), port("NAME", 0));

/*		
 		initialize("-", component("SUBTRACT"), port("OPERATIONTYPE"));
		initialize("-", component("SUBTRACTHISTOGRAM"), port("OPERATIONTYPE"));
*/
		connect(component("SUBIN"), port("OUT", 0), component("POPULATE"),
				port("INPUT"));
		/*
		// Indicator Components
		component("Short_EMA",
				com.meca.trade.components.ExponentialMovingAverage.class);
		initialize(Integer.valueOf(12), component("Short_EMA"), port("WINDOW"));

		component("Long_EMA",
				com.meca.trade.components.ExponentialMovingAverage.class);
		initialize(Integer.valueOf(26), component("Long_EMA"), port("WINDOW"));

		component("9DAY_EMA",
				com.meca.trade.components.ExponentialMovingAverage.class);
		initialize(Integer.valueOf(9), component("9DAY_EMA"), port("WINDOW"));

		
		connect(component("POPULATE"), port("OUT", 0), component("Short_EMA"),
				port("DATA"));
		connect(component("POPULATE"), port("OUT", 1), component("Long_EMA"),
				port("DATA"));
		
		connect(component("Short_EMA"), port("OUT", 0), component("SUBTRACT"),
				port("OPERAND1"));
		connect(component("Long_EMA"), port("OUT", 0), component("SUBTRACT"),
				port("OPERAND2"));

		connect(component("SUBTRACT"), port("OUT", 0), component("9DAY_EMA"),
				port("DATA"));
		connect(component("SUBTRACT"), port("OUT", 1), component("SUBOUT"),
				port("IN", 0));

		connect(component("9DAY_EMA"), port("OUT", 0), component("SUBOUT"),
				port("IN", 1));

		connect(component("SUBTRACT"), port("OUT", 2),
				component("SUBTRACTHISTOGRAM"), port("OPERAND1"));
		connect(component("9DAY_EMA"), port("OUT", 1),
				component("SUBTRACTHISTOGRAM"), port("OPERAND2"));

		connect(component("SUBTRACTHISTOGRAM"), port("OUT", 0),
				component("SUBOUT"), port("IN", 2));
*/
		

		
		connect(component("POPULATE"), port("OUT", 2), component("SUBOUT"),
				port("IN", 0));

		connect(component("POPULATE"), port("OUT", 3), component("SUBOUT"),
				port("IN", 1));
	
		connect(component("POPULATE"), port("OUT", 4), component("SUBOUT"),
				port("IN", 2));
		
		initialize("MACDLINE", component("SUBOUT"), port("NAME", 0));
		initialize("SIGNALLINE", component("SUBOUT"), port("NAME", 1));
		initialize("HISTOGRAM", component("SUBOUT"), port("NAME", 2));

	}

}
