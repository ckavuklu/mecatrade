package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants;
import com.meca.trade.to.IStrategy;
import com.meca.trade.to.PriceData;
import com.meca.trade.to.StrategyDecision;
import com.meca.trade.to.TradeUtils;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")
@OutPort(value = "OUT", description = "Output port", type = StrategyDecision.class)
//@InPort(value = "IN", arrayPort = true)

@InPorts({
	@InPort(value = "STRATEGY", description = "strategy interface", type = IStrategy.class),
	@InPort(value = "IN", arrayPort = true),
	@InPort(value = "MARKETDATA", description = "marketdata", type = PriceData.class)})

public class TradeMultiplexer extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort[] inportArray;
	
	InputPort strategyPort;
	InputPort marketDataPort;
	
	Packet pArray[];

	OutputPort outport;
	
	IStrategy strategy;

	
	@Override
	protected void execute() {

		int no = inportArray.length;
	    pArray = new Packet[no];
	    Packet marketDataPacket = null;
	    
	  
	    Packet strategyPer = null;
	    
	    if(strategy == null){
	    	strategyPer = strategyPort.receive();
			
	    	strategy = (IStrategy) strategyPer.getContent();
			
			drop(strategyPer);
			strategyPort.close();
			
	    }
	    
	    
	    while((marketDataPacket=marketDataPort.receive())!=null){
	 
	    	if(Constants.DEBUG_ENABLED)
	    		System.out.print("TradeMultData: ");

		    for (int i = 0; i < no; i++) {
		    	pArray[i] = inportArray[i].receive();
		     
		    }
		    
		    
		    PriceData marketData = (PriceData)marketDataPacket.getContent();
		    drop(marketDataPacket);
		   
		    for (int i = 0; i < no; i++) {
	    	if (pArray[i] != null) {
		    	  drop(pArray[i]);
	          }
		    }
		    
		    if(Constants.DEBUG_ENABLED)
		    	System.out.println("");

		    StrategyDecision dec = strategy.execute(pArray, marketData);
		    
		    if(Constants.DEBUG_ENABLED)
		    	System.out.println("Decision: " + dec);
		    
		    Packet p = create(dec);
			outport.send(p);
	    }

	}
	

	@Override
	protected void openPorts() {

		 strategyPort = openInput("STRATEGY");
		 
		 marketDataPort = openInput("MARKETDATA");
		
		 inportArray = openInputArray("IN");

		 outport = openOutput("OUT");
	}
}
