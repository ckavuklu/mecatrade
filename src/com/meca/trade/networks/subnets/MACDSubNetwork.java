package com.meca.trade.networks.subnets;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.SubNet;

@OutPorts({
		@OutPort(value = "MACDLINE", description = "MACD Line", type = Double.class),
		@OutPort(value = "SIGNALLINE", description = "Signal Line", type = Double.class),
		@OutPort(value = "HISTOGRAM", description = "MACD Histogram Line", type = Double.class) })

@InPorts({
	@InPort(value = "INPUT", description = "Trade Input", type = Double.class),
	@InPort(value = "SHORTEMAPERIOD", description = "Windows Size", type = Double.class),
	@InPort(value = "LONGEMAPERIOD", description = "Windows Size", type = Double.class),
	@InPort(value = "SIGNALPERIOD", description = "Windows Size", type = Double.class)
})


public class MACDSubNetwork extends SubNet {

	@Override
	protected void define() {

		component("SUBIN", com.jpmorrsn.fbp.engine.SubInComponent.class);
		component("SUBOUT", com.jpmorrsn.fbp.engine.SubOutComponent.class);
		component("POPULATE", com.meca.trade.components.PopulateInput.class);
		component("SUBTRACT", com.meca.trade.components.Calculator.class);
		component("SUBTRACTHISTOGRAM",
				com.meca.trade.components.Calculator.class);

		initialize("INPUT", component("SUBIN"), port("NAME", 0));
		initialize("SHORTEMAPERIOD", component("SUBIN"), port("NAME", 1));
		initialize("LONGEMAPERIOD", component("SUBIN"), port("NAME", 2));
		initialize("SIGNALPERIOD", component("SUBIN"), port("NAME", 3));

		
 		initialize("-", component("SUBTRACT"), port("OPERATIONTYPE"));
		initialize("-", component("SUBTRACTHISTOGRAM"), port("OPERATIONTYPE"));

		connect(component("SUBIN"), port("OUT", 0), component("POPULATE"),
				port("INPUT"));
		
		// Indicator Components
		component("Short_EMA",
				com.meca.trade.components.ExponentialMovingAverage.class);
		connect(component("SUBIN"), port("OUT", 1), component("Short_EMA"), port("WINDOW"));

		component("Long_EMA",
				com.meca.trade.components.ExponentialMovingAverage.class);
		connect(component("SUBIN"), port("OUT", 2), component("Long_EMA"), port("WINDOW"));
		
		
		component("9DAY_EMA",
				com.meca.trade.components.ExponentialMovingAverage.class);
		connect(component("SUBIN"), port("OUT", 3), component("9DAY_EMA"),
				port("WINDOW"));
		
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

		

		
		initialize("MACDLINE", component("SUBOUT"), port("NAME", 0));
		initialize("SIGNALLINE", component("SUBOUT"), port("NAME", 1));
		initialize("HISTOGRAM", component("SUBOUT"), port("NAME", 2));

	}

}
