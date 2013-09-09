package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Action;
import com.meca.trade.to.Order;
import com.meca.trade.to.StrategyDecision;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("ActionManager")
@InPort(value = "IN", description = "Input port", type = Order.class)
@OutPort(value = "CLOCKTICK", arrayPort = true)
public class ActionManager extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort inport;
	
	Packet p;

	OutputPort[] outport;

	
	@Override
	protected void execute() {
 
	    while((p = inport.receive()) != null){
	 
	    	Order value = (Order) p.getContent();
	    	drop(p);
	    	
	    	for(Action act:value.getActionList()){
	    		System.out.print("ActionManager Data: " + act + " ");
	    	}
	    	
	    	System.out.println("");
	    	
	    	//TODO: Fill in execute order
	    	//executeOrder();
	    	
	    	for (int i = 0; i < outport.length; i++) {
	    		
	    		//This is to slow down the network
	    		/*
	    		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				*/
	    		
	    		Packet clock = create(Double.NaN);
				outport[i].send(clock);
			}
	    	
	    }

	}

	

	@Override
	protected void openPorts() {

		 inport = openInput("IN");
		 outport = openOutputArray("CLOCKTICK");
	}
}
