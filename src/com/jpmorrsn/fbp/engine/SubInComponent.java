package com.jpmorrsn.fbp.engine;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

@OutPort(value = "OUT", arrayPort = true)
@InPort(value = "NAME", arrayPort = true)
public class SubInComponent extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort[] inportNameArray;

	InputPort[] inportArray;

	Component[] oldReceivers;

	Packet pNameArray[];
	Packet pOutArray[];
	Boolean initiliazed = false;

	OutputPort[] outportArray;

	@Override
	protected void execute() {

		int no = inportNameArray.length;

		if (!initiliazed) {

			inportArray = new InputPort[no];
			pNameArray = new Packet[no];
			pOutArray = new Packet[no];
			oldReceivers = new Component[no];

			for (int i = 0; i < no; i++) {
				pNameArray[i] = inportNameArray[i].receive();

				if (pNameArray[i] != null) {

					// System.out.println("SubInComponent 1" + (String)
					// pNameArray[i].getContent());
					
					if(outportArray[i].isClosed){ return; }
					
					inportArray[i] = mother.getInports().get(
							(String) pNameArray[i].getContent());
					// System.out.println("SubInComponent 2" + (String)
					// pNameArray[i].getContent());

					oldReceivers[i] = inportArray[i].getReceiver();
					
					if (inportArray[i] instanceof InitializationConnection) {
						System.out.println("NOT Supported");
					      FlowError.complain("SubinSS cannot support IIP - use Subin");
					    }

					inportArray[i].setReceiver(this);
					drop(pNameArray[i]);
				}
				
				inportNameArray[i].close();

			}
			initiliazed = true;

		}

		while (((pOutArray[0] = inportArray[0].receive()) != null)) {

			pOutArray[0].setOwner(this);

			for (int i = 1; i < no; i++) {
				pOutArray[i] = inportArray[i].receive();
				pOutArray[i].setOwner(this);
			}

			for (int i = 0; i < no; i++) {

				if (pOutArray[i] != null) {
					Double d = (Double) pOutArray[i].getContent();
					if (outportArray[i].isConnected() && d != null)
						outportArray[i].send(create(d));

					drop(pOutArray[i]);
				}
			}
		}

		
		for (int i = 0; i < no; i++) {
		    mother.traceFuncs(getName() + ": Releasing input port: " + inportArray[i].getName());
			inportArray[i].setReceiver(oldReceivers[i]);
			inportArray[i]=null;
		}
		
	}

	@Override
	protected void openPorts() {
		inportNameArray = openInputArray("NAME");
		outportArray = openOutputArray("OUT");

	}

}
