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
@InPorts({
		@InPort(value = "WINDOW", description = "Window", type = Double.class),
		@InPort(value = "DATA", description = "Data", type = Double.class) })
public class ExponentialMovingAverage extends Indicator {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort windowPort, dataPort;

	OutputPort[] outportArray;

	private Integer windowSize = null;
	private Double result = null;
	private Double previousResult = null;

	/* Multiplier: (2 / (Time periods + 1) ) = (2 / (10 + 1) ) = 0.1818 (18.18%) */
	Double multiplier = 0d;

	@Override
	protected void execute() {

		Packet dataPacket = null;
		Packet windowPacket = null;

		if (windowSize == null) {
			windowPacket = windowPort.receive();
			Double doubleValue = (Double) windowPacket.getContent();
			windowSize = new Integer(doubleValue.intValue());
			multiplier = 2d / (windowSize + 1);

			windowPort.close();
			drop(windowPacket);
		}

		while ((dataPacket = dataPort.receive()) != null) {

			Double dat = (Double) dataPacket.getContent();

			if (dat.isNaN()) {
				result = Double.NaN;
			} else {
				window.add(window.size(), dat);

				result = average();
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

		outportArray = openOutputArray("OUT");

	}

	protected Double average() {
		Double result = 0d;

		if (previousResult == null) {
			/* This is SMA evaluation */
			if (window.size() >= windowSize) {

				for (Double data : window) {
					result += Double.valueOf(data);
				}

				previousResult = result = (result / windowSize);

				window.clear();

			} else
				result = Double.NaN;
		} else {
			/*
			 * EMA: {Close - EMA(previous day)} x multiplier + EMA(previous
			 * day).
			 */

			result = ((window.get(0) - previousResult) * multiplier)
					+ previousResult;

			previousResult = result;
			window.clear();
		}

		return result;
	}
}
