package com.jpmorrsn.fbp.engine;

import java.util.ArrayList;
import java.util.List;

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

	List<Integer> nonInitializationList = null;
	OutputPort[] outportArray;

	@Override
	protected void execute() {

		int no = inportNameArray.length;

		if (!initiliazed) {

			nonInitializationList = new ArrayList<Integer>();
			inportArray = new InputPort[no];
			pNameArray = new Packet[no];
			pOutArray = new Packet[no];
			oldReceivers = new Component[no];

			for (int i = 0; i < no; i++) {
				pNameArray[i] = inportNameArray[i].receive();

				if (pNameArray[i] != null) {

					if (outportArray[i].isClosed) {
						return;
					}

					inportArray[i] = mother.getInports().get(
							(String) pNameArray[i].getContent());

					oldReceivers[i] = inportArray[i].getReceiver();

					if (!(inportArray[i] instanceof InitializationConnection))
						inportArray[i].setReceiver(this);

					drop(pNameArray[i]);
				}

				inportNameArray[i].close();

			}
			initiliazed = true;

		}

		for (int i = 0; i < no; i++) {

			if (inportArray[i] instanceof InitializationConnection) {

				InitializationConnection iico = (InitializationConnection) inportArray[i];
				InitializationConnection iic = new InitializationConnection(
						iico.content, this);
				iic.name = iico.name;

				pOutArray[i] = iic.receive();
				pOutArray[i].setOwner(this);
				outportArray[i].send(pOutArray[i]);
				iic.close();
//				iic = null;
			} else
				nonInitializationList.add(i);

		}

		if (nonInitializationList.size() > 0) {

			while (((pOutArray[nonInitializationList.get(0)] = inportArray[nonInitializationList
					.get(0)].receive()) != null)) {

				pOutArray[nonInitializationList.get(0)].setOwner(this);

				for (int i = 1; i < nonInitializationList.size(); i++) {
					pOutArray[nonInitializationList.get(i)] = inportArray[nonInitializationList
							.get(i)].receive();
					pOutArray[nonInitializationList.get(i)].setOwner(this);
				}

				for (int i = 0; i < nonInitializationList.size(); i++) {

					if (pOutArray[nonInitializationList.get(i)] != null) {
						outportArray[nonInitializationList.get(i)]
								.send(pOutArray[nonInitializationList.get(i)]);
					}

				}
			}
		}
		for (int i = 0; i < nonInitializationList.size(); i++) {

			inportArray[nonInitializationList.get(i)]
					.setReceiver(oldReceivers[nonInitializationList.get(i)]);
			
		}
		
		
		for (int i = 0; i < no; i++)
			inportArray[i] = null;
	}

	@Override
	protected void openPorts() {
		inportNameArray = openInputArray("NAME");
		outportArray = openOutputArray("OUT");

	}

}
