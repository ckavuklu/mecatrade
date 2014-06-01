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
@ComponentDescription("outputs static parameters")

@OutPort(value = "OUT", description = "OUT", type = Double.class)


@InPorts({
	@InPort(value = "IN", description = "data", type = Double.class),
	@InPort(value = "DATA", description = "data", type = Double.class)})

public class StaticParameter extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort inport;
	
	InputPort marketDataPort;
	
	Packet packet = null;

	OutputPort outport;
	
	Double parameter;
	
	
	@Override
	protected void execute() {

	    Packet marketDataPacket = null;

		if (parameter == null) {
			packet = inport.receive();
			parameter = (Double) packet.getContent();
			drop(packet);
			inport.close();
		}
		          
	    while((marketDataPacket=marketDataPort.receive())!=null){
	 
	       drop(marketDataPacket);
		   

				if (outport.isConnected()) {
					outport.send(create(parameter));
				}
	       }
	    
	    marketDataPort.close();

	}
	

	@Override
	protected void openPorts() {

		 marketDataPort = openInput("DATA");
		
		 inport = openInput("IN");

		 outport = openOutput("OUT");
	}
}
