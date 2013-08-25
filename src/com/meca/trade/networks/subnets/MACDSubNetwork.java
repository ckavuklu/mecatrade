package com.meca.trade.networks.subnets;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.SubNet;
import com.meca.trade.to.TradeData;

@OutPorts({
	@OutPort(value = "PRICETYPE", description = "type", type = String.class),
	@OutPort(value = "TRADEDATA", description = "trade data", type = TradeData.class) })
@InPort("IN")
public class MACDSubNetwork extends SubNet {

	@Override
	  protected void define() {
	    
		
		
		 component("SUBIN", com.jpmorrsn.fbp.engine.SubInComponent.class); 
		 component("SUBOUT", com.jpmorrsn.fbp.engine.SubOutComponent.class); // do.
		    
		 
	    initialize("IN", component("SUBIN"), port("IN",0));
	    
	    initialize("OUT", component("SUBOUT"), port("OUT",0));
		    
		    
		
	    // Indicator Components
	    component("_ExponentialMovingAverage", com.meca.trade.components.ExponentialMovingAverage.class);
	    
	  
	    initialize(Integer.valueOf(10), component("_ExponentialMovingAverage"), port("WINDOW"));
	    	    
	    connect(component("_ExponentialMovingAverage"), port("OUT"), component("_TradeMultiplexer"), port("IN",5));
	    
	  }

}
