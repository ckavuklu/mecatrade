package com.meca.trade.components;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Simple Moving Average")
@OutPort(value = "OUT", description = "Output port", type = Double.class)
@InPorts({
		@InPort(value = "WINDOW", description = "Window", type = String.class),
		@InPort(value = "DATA", description = "Data", type = Double.class) })
public class SimpleMovingAverage extends Indicator {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort windowPort, dataPort;

	OutputPort outport;

	private Integer windowSize = null;
	private Double result = null;
	
	@Override
	protected void execute() {

		Packet dataPacket = null;
		Packet windowPacket = null;

		if (windowSize == null) {
			windowPacket = windowPort.receive();
			windowSize = new Integer ((Integer)windowPacket.getContent());
			
			windowPort.close();
			drop(windowPacket);
		}
		
		

		while ((dataPacket = dataPort.receive()) != null) {

			Double dat = (Double) dataPacket.getContent();

			window.add(window.size(),dat);
			
			if(window.size() >= windowSize){
				result = average();
				window.remove(0);
			} else result = Double.NaN; 
			
			try {
				
				if(result != null && outport.isConnected()){
					outport.send(create(result));
					//System.out.println("QuotePrice("+priceType+"): " + result + " ");
				}
				
				drop(dataPacket);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		dataPort.close();
		//outport.close();
	}

	@Override
	protected void openPorts() {

		windowPort = openInput("WINDOW");
		dataPort = openInput("DATA");

		outport = openOutput("OUT");

	}
	
	private Double average(){
		Double result = 0d;
		
		
		for(Double data:window){
			// System.out.print(data + " ");
			result += Double.valueOf(data); 
		} 
		
		return result/windowSize;
	}
}
