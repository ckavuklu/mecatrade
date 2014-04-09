package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/* Indicator source: http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages */

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Relative Strength Index")
@OutPort(value = "RSI", arrayPort = true)
@InPorts({
		@InPort(value = "WINDOW", description = "Window", type = Double.class),
		@InPort(value = "DATA", description = "Data", type = Double.class) })


public class RelativeStrengthIndex extends Indicator {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort windowPort, dataPort;

	OutputPort[] outportArray;

	private Integer windowSize = null;
	private Double result = null;
	private Double previousData = null;
	private Double previousAverageGain = null;
	private Double previousAverageLoss = null;


	@Override
	protected void execute() {

		Packet dataPacket = null;
		Packet windowPacket = null;

		
		if (windowSize == null) {
			windowPacket = windowPort.receive();
			Double doubleValue = (Double) windowPacket.getContent();
			windowSize = new Integer(doubleValue.intValue());
			
			windowPort.close();
			drop(windowPacket);
		}

		while ((dataPacket = dataPort.receive()) != null) {

			Double dat = (Double) dataPacket.getContent();

			if (dat.isNaN()) {
				result = Double.NaN;
			} else {
				
				if (previousData!=null)
					window.add(dat-previousData);
				
				previousData = dat;
				
				result = rsi();
			}
			
			try {
				if (result != null) {

					for (int i = 0; i < outportArray.length; i++) {

						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(result));
						}
					}

					// System.out.println("QuotePrice("+priceType+"): " + result
					// + " ");

				}
				drop(dataPacket);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		dataPort.close();
		// outport.close();
	}

	@Override
	protected void openPorts() {

		windowPort = openInput("WINDOW");
		dataPort = openInput("DATA");

		outportArray = openOutputArray("RSI");

	}

	private Double rsi() {
		Double result = 0d;

		Double gain = 0d;
		Double loss = 0d;
		Double rs = 0d;
		
		if(previousAverageGain == null || previousAverageLoss==null){
		
			if (this.window.size() >= windowSize) {

				for (Double data : window) {
					if (data > 0d ) gain += data; 
						else loss += Math.abs(data);
				
				}

				if (loss == 0d) rs = Double.MAX_VALUE;
				else rs = (gain) / (loss);
				
				previousAverageGain = gain/windowSize;
				previousAverageLoss = loss/windowSize;
				
				result = 100 - 100 / (1 + rs);

				window.remove(0);

			} else

				result = Double.NaN;
		}else{
			Double averageLoss = ((previousAverageLoss*(windowSize-1)) + Math.abs(window.get(windowSize-1)<0d?window.get(windowSize-1):0d))/windowSize;
			Double averageGain = ((previousAverageGain*(windowSize-1)) + (window.get(windowSize-1)>0d?window.get(windowSize-1):0d))/windowSize;
			
			if (averageLoss == 0d) 
				rs = Double.MAX_VALUE;
			else
				rs = averageGain / averageLoss;
			
			previousAverageGain = averageGain;
			previousAverageLoss = averageLoss;
			
			result = 100 - 100 / (1 + rs);

			window.remove(0);
		}
		

		return result;
	}
}
