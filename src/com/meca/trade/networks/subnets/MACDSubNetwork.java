package com.meca.trade.networks.subnets;

import com.jpmorrsn.fbp.engine.SubNet;

public class MACDSubNetwork extends SubNet {

	@Override
	  protected void define() {
	    
		

	    
	    // Indicator Components
	    component("_ExponentialMovingAverage", com.meca.trade.components.ExponentialMovingAverage.class);
	    
	  
	    initialize(Integer.valueOf(10), component("_ExponentialMovingAverage"), port("WINDOW"));
	    	    
	    connect(component("_ExponentialMovingAverage"), port("OUT"), component("_TradeMultiplexer"), port("IN",5));
	    
	  }

}
