package com.jpmorrsn.fbp.engine;

import java.util.Collection;
import java.util.HashMap;

@InPorts({ @InPort(value = "IN", arrayPort = true),
		@InPort(value = "NAME", arrayPort = true) })
public class SubOutComponent extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort[] inportNameArray;

	InputPort[] inportArray;

	Packet pNameArray[];
	Packet pOutArray[];
	Boolean initiliazed = false;
	Packet p;

	OutputPort[] outportArray;

	@Override
	protected void execute() {
		int no = inportNameArray.length;

		if (!initiliazed) {

			outportArray = new OutputPort[no];
			pNameArray = new Packet[no];
			inportArray = new InputPort[no];
			pOutArray = new Packet[no];

			for (int i = 0; i < no; i++) {

				pNameArray[i] = inportNameArray[i].receive();

				if (pNameArray[i] != null) {

					String name = (String) pNameArray[i].getContent();
					// HashMap<String, OutputPort> hash = ;
					outportArray[i] = mother.getOutports().get(name);
					outportArray[i].setSender(this);

					 p = create(Packet.OPEN, "");
					 outportArray[i].send(p);

					 System.out.println("SubOutComponent Input Port " + name);

					drop(pNameArray[i]);
				}
				inportNameArray[i].close();

			}

			initiliazed = true;

		}

		while (((pOutArray[0] = inportArray[0].receive()) != null)) {

			// pOutArray[0].setOwner(this);

			System.out.println("SubOutComponent output "
					+ (Double) pOutArray[0].getContent());

			for (int i = 1; i < no; i++) {
				pOutArray[i] = inportArray[i].receive();
			}

			for (int i = 0; i < no; i++) {
				if (pOutArray[i] != null) {
					Double d = (Double) pOutArray[i].getContent();
					if (outportArray[i].isConnected() && d != null)
						outportArray[i].send(create(d));
				}

			}
		}

		for (int i = 0; i < no; i++) {
			p = create(Packet.CLOSE, "");
			outportArray[i].send(p);
			outportArray[i] = null;
		}

	}

	@Override
	protected void openPorts() {
		inportNameArray = openInputArray("NAME");
		inportArray = openInputArray("IN");

	}

}
