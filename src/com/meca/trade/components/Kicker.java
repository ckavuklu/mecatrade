package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")

@OutPort(value = "OUT", arrayPort = true)

public class Kicker extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

    OutputPort[] outportArray;

	private Double result = null;
	
	@Override
	protected void execute() {

		Packet p = null;
		Packet c = null;
		Packet pricePacket = null;
		result = new Double(Double.NaN);

			try {
				
					for(int i=0;i<outportArray.length;i++){
					
						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(result));
						} 
					}
					
					//System.out.println("QuotePrice("+priceType+"): " + result + " ");
				
				}
	

		   catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	

	@Override
	protected void openPorts() {

		outportArray = openOutputArray("OUT");

	}
}
