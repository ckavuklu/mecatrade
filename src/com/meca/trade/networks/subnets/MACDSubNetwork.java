package com.meca.trade.networks.subnets;

import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.SubNet;
import com.meca.trade.to.TradeData;

@OutPorts({
	@OutPort(value = "MACDLINE", description = "MACD Line", type = Double.class),
	@OutPort(value = "SIGNALLINE", description = "Signal Line", type = Double.class),
	@OutPort(value = "HISTOGRAM", description = "MACD Histogram Line", type = Double.class) 
	})

@InPort("IN")


public class MACDSubNetwork extends SubNet {

	@Override
	  protected void define() {
	    
		
		
		 component("SUBIN", com.jpmorrsn.fbp.engine.SubInComponent.class); 
		 component("SUBOUT", com.jpmorrsn.fbp.engine.SubOutComponent.class); // do.
		    
		 
	    initialize("IN", component("SUBIN"), port("NAME",0));
	    
	    initialize("MACDLINE", component("SUBOUT"), port("OUT",0));
	    initialize("SIGNALLINE", component("SUBOUT"), port("OUT",1));
	    initialize("HISTOGRAM", component("SUBOUT"), port("OUT",2));
		
	    
		
	    // Indicator Components
	    component("_ExponentialMovingAverage", com.meca.trade.components.ExponentialMovingAverage.class);
	    
	  
	    initialize(Integer.valueOf(10), component("_ExponentialMovingAverage"), port("WINDOW"));
	    	
	    
	    connect(component("_ExponentialMovingAverage"), port("OUT"), component("_TradeMultiplexer"), port("IN",5));
	    
	  }

}
