package com.meca.trade.components;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to generate a stream of 'n' packets, where 'n' is
* specified in an InitializationConnection.
*/
@ComponentDescription("Generates stream of packets under control of a counter")
@OutPort(value = "OUT", description = "Generated data", type = String.class)
@InPort(value = "FILENAME", description = "FileName", type = String.class)
public class DataSource extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  OutputPort outport;

  InputPort fileName;

  @Override
  protected void execute() {
    Packet ctp = fileName.receive();
    
    if (ctp == null) {
      return;
    }
    
    //fileName.close();

    String cti = (String) ctp.getContent();
    cti = cti.trim();
    int ct = 500;
    
    BufferedReader br = null;
    
    try {
		br = new BufferedReader(new FileReader(cti));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}finally{
		drop(ctp);
	}
    
    
  }

  String repeat(final String string, final int ct) {
    String result = "";
    for (int i = 0; i < ct; i++) {
      result = result + string;
    }
    return result;
  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    fileName = openInput("FILENAME");
  }
}
