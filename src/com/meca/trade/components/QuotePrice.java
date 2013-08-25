package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.TradeData;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")

@OutPort(value = "OUT", arrayPort = true)

@InPorts({
		@InPort(value = "PRICETYPE", description = "type", type = String.class),
		@InPort(value = "TRADEDATA", description = "trade data", type = TradeData.class) })
public class QuotePrice extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort priceTypePort, tradeDataPort;

    OutputPort[] outportArray;

	private String priceType = null;
	private Double result = null;
	
	@Override
	protected void execute() {

		Packet p = null;
		Packet pricePacket = null;

		if (priceType == null) {
			pricePacket = priceTypePort.receive();
			this.priceType = String.valueOf((String) pricePacket
					.getContent());
			
			priceTypePort.close();
			drop(pricePacket);
		}
		
		

		while ((p = tradeDataPort.receive()) != null) {

			TradeData dat = (TradeData) p.getContent();
						
			if(priceType.equalsIgnoreCase("O")){
				result = Double.valueOf(dat.getOpen());
			} else if(priceType.equalsIgnoreCase("C")){
				result = Double.valueOf(dat.getClose());
			} else if(priceType.equalsIgnoreCase("H")){
				result = Double.valueOf(dat.getHigh());
			} else if(priceType.equalsIgnoreCase("L")){
				result = Double.valueOf(dat.getLow());
			} 
			

			try {
				
				if(result != null){
					
					for(int i=0;i<outportArray.length;i++){
					
						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(result));
						} 
					}
					
					//System.out.println("QuotePrice("+priceType+"): " + result + " ");
				
				}
				
				drop(p);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		tradeDataPort.close();
		//outport.close();
	}

	@Override
	protected void openPorts() {

		priceTypePort = openInput("PRICETYPE");
		tradeDataPort = openInput("TRADEDATA");

		outportArray = openOutputArray("OUT");

	}
}
