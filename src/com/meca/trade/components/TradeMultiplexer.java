package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")
//@OutPort(value = "OUT", description = "Output port", type = Double.class)
@InPort(value = "IN", arrayPort = true)
public class TradeMultiplexer extends Component {

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
	 
	    	System.out.print("TradeMultData: ");
	    	
		    for (int i = 1; i < no; i++) {
		    	pArray[i] = inportArray[i].receive();
		     
		    }
		    
		    for (int i = 0; i < no; i++) {
	    	if (pArray[i] != null) {
		    	  Double value = (Double) pArray[i].getContent();
		    	  System.out.print(value + " ");
		    	  drop(pArray[i]);
	          }
	    	  
		    }
			
		    System.out.println("");
	    
	    }
	    
	    
	    		
		
	}

	@Override
	protected void openPorts() {

		 inportArray = openInputArray("IN");

		 //outport = openOutput("OUT");
	}
}
