package com.meca.trade.components;

import java.util.Random;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.DecisionType;
import com.meca.trade.to.Order;
import com.meca.trade.to.QuoteType;
import com.meca.trade.to.StrategyDecision;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Messages")
@OutPort(value = "OUT", description = "Output port", type = Order.class)
@InPort(value = "IN", arrayPort = true)
public class PortolioManager extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort[] inportArray;
	
	Packet pArray[];

	OutputPort outport;

	
	@Override
	protected void execute() {

		int no = inportArray.length;
	    pArray = new Packet[no];
	    
	  
	    
	    while((pArray[0] = inportArray[0].receive()) != null){
	 
	    	//System.out.print("Portfolio Data: ");
	    	
		    for (int i = 1; i < no; i++) {
		    	pArray[i] = inportArray[i].receive();
		     
		    }
		    
		    for (int i = 0; i < no; i++) {
	    	if (pArray[i] != null) {
	    		  StrategyDecision value = (StrategyDecision) pArray[i].getContent();
		    	  //System.out.print(value + " ");
		    	  drop(pArray[i]);
		    	  
		    	  
		    	  
	          }
	    	  
		    }
		    
		    //System.out.println("");
		    
			
		    Packet p = create(setOrder());
			outport.send(p);
	    }
	    
	    
	    		
		
	}
	
	private Order setOrder(){	
		return new Order((new Random().nextInt(2)>0)?DecisionType.LONG:DecisionType.SHORT,QuoteType.EURUSD,1d);
		
	}

	@Override
	protected void openPorts() {

		 inportArray = openInputArray("IN");

		 outport = openOutput("OUT");
	}
}