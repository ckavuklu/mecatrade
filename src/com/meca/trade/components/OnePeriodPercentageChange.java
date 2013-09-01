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
@ComponentDescription("Exponential Moving Average")
@OutPort(value = "OUT", arrayPort = true)
@InPort(value = "INPUT", description = "Data", type = Double.class) 

public class OnePeriodPercentageChange extends Indicator {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort dataPort;

	OutputPort[] outportArray;

	private Double previousResult = null;
	private Double percentChange = null;


	@Override
	protected void execute() {

		Packet dataPacket = null;

		while ((dataPacket = dataPort.receive()) != null) {

			Double dat = (Double) dataPacket.getContent();

			if (dat.isNaN()) {
			
				percentChange = Double.NaN;

			} else {
				
				if (!previousResult.isNaN())
					percentChange = ((dat - previousResult) / dat) * 100;
			}
			
			previousResult = dat;

			try {
				if (percentChange != null) {

					for (int i = 0; i < outportArray.length; i++) {

						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(percentChange));
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

		dataPort = openInput("INPUT");

		outportArray = openOutputArray("OUT");

	}
}
