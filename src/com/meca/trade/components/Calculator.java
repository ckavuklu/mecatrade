package com.meca.trade.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.MarketData;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")
@OutPort(value = "OUT", arrayPort = true)
@InPorts({
		@InPort(value = "OPERATIONTYPE", description = "operation type", type = String.class),
		@InPort(value = "OPERAND1", description = "trade data", type = Double.class),
		@InPort(value = "OPERAND2", description = "trade data", type = Double.class) })
public class Calculator extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort operatorPort, operand1Port, operand2Port;

	OutputPort[] outportArray;

	private String operandType = null;
	private Double result = null;

	@Override
	protected void execute() {

		Packet p1 = null;
		Packet p2 = null;
		Packet operandPacket = null;
		Double double1 = Double.NaN;
		Double double2 = Double.NaN;

		if (operandType == null) {
			operandPacket = operatorPort.receive();
			this.operandType = String.valueOf((String) operandPacket
					.getContent());

			operatorPort.close();
			drop(operandPacket);
		}

		while ((p1 = operand1Port.receive()) != null
				&& (p2 = operand2Port.receive()) != null) {

			double1 = (Double) p1.getContent();
			double2 = (Double) p2.getContent();

			if (Double.isNaN(double1) || Double.isNaN(double2))
				result = Double.NaN;

			else {

				if (operandType.equalsIgnoreCase("+")) {
					result = double1 + double2;
				} else if (operandType.equalsIgnoreCase("-")) {
					result = double1 - double2;
				} else if (operandType.equalsIgnoreCase("*")) {
					result = double1 * double2;
				} else if (operandType.equalsIgnoreCase("/")) {
					result = double1 / double2;
				}
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

				drop(p1);
				drop(p2);
			}

			catch (Exception e) {
				e.printStackTrace();
			}
		}

		operand1Port.close();
		operand2Port.close();
		// outport.close();
	}

	@Override
	protected void openPorts() {

		operand1Port = openInput("OPERAND1");
		operand2Port = openInput("OPERAND2");
		operatorPort = openInput("OPERATIONTYPE");
		outportArray = openOutputArray("OUT");

	}
}
