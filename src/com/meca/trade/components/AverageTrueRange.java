package com.meca.trade.components;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.TradeUtils;

/** Average True Range **/
@ComponentDescription("Average True Range")
@OutPort(value = "ATR", arrayPort = true)

@InPorts({
		@InPort(value = "HIGH", description = "High Data", type = Double.class),
		@InPort(value = "LOW", description = "Low Data", type = Double.class),
		@InPort(value = "CLOSE", description = "Close Data", type = Double.class),
		@InPort(value = "WINDOW", description = "Window Size", type = Double.class)
})
public class AverageTrueRange extends Indicator {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort windowPort, closeDataPort, highDataPort, lowDataPort;

	
	OutputPort[] outport;
	
	private Double windowSize = null;
	
	private Double result = null;
	
	private Double TR = null;
	
	private Double previousClose = null;
	
	private Double priorATR = null;
	
	@Override
	protected void execute() {

		Packet highDataPacket = null;
		Packet lowDataPacket = null;
		Packet windowPacket = null;
		Packet closeDataPacket = null;
		
		if (windowSize == null) {
			windowPacket = windowPort.receive();
			Double doubleValue = (Double) windowPacket.getContent();
			windowSize = doubleValue;
			windowPort.close();
			drop(windowPacket);
		}
		
		
		while ((highDataPacket = highDataPort.receive()) != null) {

			lowDataPacket = lowDataPort.receive();
			closeDataPacket = closeDataPort.receive();
			
			Double hDat = (Double) highDataPacket.getContent();
			Double lDat = (Double) lowDataPacket.getContent();
			Double cDat = (Double) closeDataPacket.getContent();

			TR = Math.max((hDat - lDat), previousClose!=null?(Math.max(Math.abs(hDat - previousClose), Math.abs(lDat - previousClose))):0);
			
			if(window.size() < windowSize){
				window.add(TR);
			}
			
			if(window.size() == windowSize){
				if (priorATR == null){
					result = average();
				}else{
					result = (((priorATR * (windowSize-1)) + TR) / windowSize);
				}
				priorATR = result;
			}
				
			previousClose = cDat;
			
			try {
				
				if(result != null){
					for(int i=0;i<outport.length;i++){
						
						if (outport[i].isConnected()) {
							outport[i].send(create(result));
						} 
					}
				}
				
			
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally{
				drop(highDataPacket);
				drop(lowDataPacket);
				drop(closeDataPacket);
			}
		}
		
		highDataPort.close();
		lowDataPort.close();
		closeDataPort.close();
	}

	@Override
	protected void openPorts() {

		closeDataPort = openInput("CLOSE");
		windowPort = openInput("WINDOW");
		highDataPort = openInput("HIGH");
		lowDataPort = openInput("LOW");

		outport = openOutputArray("ATR");
	}
	
}
