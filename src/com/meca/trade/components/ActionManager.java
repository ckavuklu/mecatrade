package com.meca.trade.components;

import java.util.Date;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.IPositionManager;
import com.meca.trade.to.Order;
import com.meca.trade.to.Trade;
import com.meca.trade.to.TradeStatusType;
import com.meca.trade.to.TradeType;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("ActionManager")


@InPorts({
	@InPort(value = "MANAGER", description = "position manafer interface", type = IPositionManager.class),
	@InPort(value = "IN", description = "Input port", type = Order.class) })

@OutPort(value = "CLOCKTICK", arrayPort = true)
public class ActionManager extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort inport;
	
	Packet p;

	OutputPort[] outport;
	
	InputPort marketManagerPort;
	
	IPositionManager manager = null;

	
	@Override
	protected void execute() {
		
		Packet managerPer = null;
	    
	    if(manager == null){
	    	managerPer = marketManagerPort.receive();
			
			manager = (IPositionManager) managerPer.getContent();
			
			drop(managerPer);
			marketManagerPort.close();
	    }
 
	    while((p = inport.receive()) != null){
	 
	    	Order value = (Order) p.getContent();
	    	drop(p);
	    	
	    	/*
	    	for(Trade act:value.getTradeList()){
	    		System.out.print("ActionManager Data: " + act + " ");
	    	}
	    	
	    	System.out.println("");
	    	*/
	    	
	    	//This is to slow down the network
	    	/*try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
	    	
	    	//TODO: Fill in execute order
	    	placeMarketOrder(value);
			
	    	
	    	for (int i = 0; i < outport.length; i++) {
	    		
	    		
	    		Packet clock = create(Double.NaN);
				outport[i].send(clock);
			}
	    	
	    	
	    }
	}

	private void placeMarketOrder(Order order){
		
		//TODO: Market Interface Implementation for Order. We will get market data as realized price
		
		for(Trade trade : order.getTradeList()){
			trade.setRealizedDate(trade.getEntryDate());
			trade.setRealizedPrice((trade.getTradeType() == TradeType.LEXIT || trade.getTradeType() == TradeType.SEXIT)?trade.getExitPrice():trade.getEntryPrice());
			trade.setStatus(TradeStatusType.CLOSE);
			manager.realizeTrade(trade);
		}
	}
	

	@Override
	protected void openPorts() {

		 inport = openInput("IN");
		 marketManagerPort = openInput("MANAGER");
		 outport = openOutputArray("CLOCKTICK");
	}
}
