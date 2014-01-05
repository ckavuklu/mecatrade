package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Populate Input Messages")

@OutPort(value = "OUT", arrayPort = true)

@InPort(value = "INPUT", description = "Input data", type = Double.class)

public class PopulateInput extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort tradeDataPort;
    OutputPort[] outportArray;
	
	@Override
	protected void execute() {

		Packet p = null;
		Double result;
		
		while ((p = tradeDataPort.receive()) != null) {

			try {
				result = (Double) p.getContent();
				
				if(result != null){
					
					for(int i=0;i<outportArray.length;i++){
					
						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(result));
						} 
					}
				
				}
				
				drop(p);

			} catch (Exception e) {
				if(Constants.DEBUG_ENABLED)
					System.out.println("PopulateInput.terminated()");
				e.printStackTrace();
			}
		}
		
		tradeDataPort.close();
		//outport.close();
	}

	@Override
	protected void openPorts() {

		tradeDataPort = openInput("INPUT");
		outportArray = openOutputArray("OUT");

	}
}
