package com.meca.trade.components;

import java.io.IOException;
import java.util.Iterator;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.Constants;
import com.meca.trade.to.PriceData;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Filters Messages")
@OutPort(value = "OUT", arrayPort = true)
@InPorts({
	@InPort(value = "KICKOFF", description = "type", type = Double.class),
	@InPort(value = "CLOCKTICK", description = "type", type = Double.class),
		@InPort(value = "ITERATOR", description = "Iterator", type = Iterator.class)
})
public class DataFeeder extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort tradeDataPort, kickoffPort, clockTickPort;

	OutputPort[] outportArray;
	InputPort iteratorPort;
	

	@Override
	protected void execute() {

		Packet kickoffPacket = null;
		boolean kickOff = false;
		Iterator<PriceData> iterator = null;
		Packet ctp = null;
		Packet c = null;

		if (ctp == null) {	
			ctp = iteratorPort.receive();
			iterator = ((Iterator<PriceData>) ctp.getContent());
		    iteratorPort.close();
			drop(ctp);
		}
		
		if (kickoffPacket == null) {
			kickoffPacket = kickoffPort.receive();
			kickOff = true;
			kickoffPort.close();
			drop(kickoffPacket);
		}

		
		try{
			while(iterator.hasNext()){
				PriceData data = iterator.next();
				
				if(Constants.DEBUG_ENABLED)
					System.out.println("DataFeeder: " + data);
				
				for (int i = 0; i < outportArray.length; i++) {
					
					if (outportArray[i].isConnected()) {
						outportArray[i].send(create(data));
					}
				}
				
				//This is to wait for the clock tick
				c = clockTickPort.receive();
				drop(c);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			clockTickPort.close();
		}
		/*
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(iterator));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 

		String line = null;
		try {

			while ((line = br.readLine()) != null && kickOff) {

				try {
					
					String[] trade = line.split(",");

					PriceData data = new PriceData(TradeUtils.getDouble(trade[3]),TradeUtils.getDouble(trade[6]),TradeUtils.getDouble(trade[4]),TradeUtils.getDouble(trade[5]));
					Date date = TradeUtils.getTime(trade[1] + "-" + trade[2]);
					data.setQuote(trade[0]);	
					data.setTime(date);
					data.setVolume(TradeUtils.getDouble(trade[7]));

					if(Constants.DEBUG_ENABLED)
						System.out.println("DataFeeder: " + data);
					
					for (int i = 0; i < outportArray.length; i++) {
						
						if (outportArray[i].isConnected()) {
							outportArray[i].send(create(data));
						}
					}
					
					//This is to wait for the clock tick
					c = clockTickPort.receive();
					drop(c);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			clockTickPort.close();
		}
		
		*/

	}

	@Override
	protected void openPorts() {
		iteratorPort = openInput("ITERATOR");
		clockTickPort = openInput("CLOCKTICK");
		kickoffPort = openInput("KICKOFF");

		outportArray = openOutputArray("OUT");

	}
}
