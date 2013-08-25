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

	Packet pNameArray[];
	Packet pOutArray[];

	OutputPort[] outportArray;

	@Override
	protected void execute() {
		int no = inportNameArray.length;
		int out = outportArray.length;
		
		pNameArray = new Packet[no];
		inportArray = new InputPort[no];
		pOutArray = new Packet[no];

		for (int i = 0; i < no; i++) {
			pNameArray[i] = inportNameArray[i].receive();
			
			if (pNameArray[i] != null) {

				inportArray[i] = mother.getInports().get(
						(String) pNameArray[i].getContent());
				
				inportArray[i].setReceiver(this);
				drop(pNameArray[i]);
			}

		}

		
		while(((pOutArray[0] = inportArray[0].receive()) != null)){
			pOutArray[0].setOwner(this);
			
			 for (int i = 1; i < no; i++) {
				 pOutArray[i] = inportArray[i].receive();
		     }
			
			for (int i = 0; i < out; i++) {
				if (pOutArray[i] != null) {
					if (outportArray[i].isConnected()) 
						outportArray[i].send(create(pOutArray[i]));				
					
					drop(pOutArray[i]);
				}
			}
		}

	}

	@Override
	protected void openPorts() {
		inportNameArray = openInputArray("NAME");
		outportArray = openOutputArray("OUT");

	}

}
