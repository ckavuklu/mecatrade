package com.meca.trade.networks.subnets;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.SubNet;
import com.meca.trade.to.TradeData;

@OutPort(value = "MACDLINE", description = "MACD Line", type = Double.class)
@InPort(value = "INPUT", description = "Trade Input", type = Double.class)
public class TrialSubNetwork extends SubNet {

	@Override
	protected void define() {

		component("SUBIN", com.jpmorrsn.fbp.engine.SubInComponent.class);
		component("SUBOUT", com.jpmorrsn.fbp.engine.SubOutComponent.class);

		initialize("INPUT", component("SUBIN"), port("NAME", 0));

		initialize("MACDLINE", component("SUBOUT"), port("NAME", 0));
		
		
		connect(component("SUBIN"), port("OUT", 0), component("SUBOUT"),
				port("IN", 0));


	}

}
