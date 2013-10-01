package com.meca.trade.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.IMarketManager;
import com.meca.trade.to.Order;
import com.meca.trade.to.SignalType;
import com.meca.trade.to.StrategyDecision;
import com.meca.trade.to.Trade;
import com.meca.trade.to.TradeStatusType;
import com.meca.trade.to.TradeType;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Messages")
@OutPort(value = "OUT", description = "Output port", type = Order.class)

@InPorts({
	@InPort(value = "MARKETMANAGER", description = "market manafer interface", type = IMarketManager.class),
	@InPort(value = "IN", arrayPort = true) })



public class PortolioManager extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort marketManagerPort;
	
	InputPort[] inportArray;
	
	Packet pArray[];

	OutputPort outport;
	
	IMarketManager manager = null;

	
	@Override
	protected void execute() {

		int no = inportArray.length;
	    pArray = new Packet[no];
	    List<StrategyDecision> strategyDecisions = new ArrayList<StrategyDecision>();
	    
	    Packet managerPer = null;
	    
	    if(manager == null){
	    	managerPer = marketManagerPort.receive();
			
			manager = (IMarketManager) managerPer.getContent();
			
			drop(managerPer);
			marketManagerPort.close();
	    }
	  
	    
	    
	    while((pArray[0] = inportArray[0].receive()) != null){
	    	
	    	//System.out.print("Portfolio Data: ");
	    	
		    for (int i = 1; i < no; i++) {
		    	pArray[i] = inportArray[i].receive();
		    }
		    
		    for (int i = 0; i < no; i++) {
		    
	    	if (pArray[i] != null) {
	    		  StrategyDecision value = (StrategyDecision) pArray[i].getContent();
	    		  strategyDecisions.add(value);
		    	  //System.out.print(value + " ");
	    		  
		    	  drop(pArray[i]);
	          }
		    }
		    
		    Order order = new Order(executeTrades(evaluateDecisions(strategyDecisions)));
		    
	    	strategyDecisions.clear();
	    	
		    
		    //System.out.println("");
		    
		    Packet p = create(order);
			outport.send(p);
	    }

	}
	
	private List<Trade> evaluateDecisions(final List<StrategyDecision> decisionList){
		Trade trade = new Trade(TradeType.Buy,SignalType.En,10d,12d,new Date(),TradeStatusType.OPEN);
		
		List<Trade> tradeList = new ArrayList<Trade>();
		
		/*TODO*/
		
		tradeList.add(trade);
		
		return tradeList;
	}
	
	
	private List<Trade> executeTrades(final List<Trade> tradeList){
		List<Trade> resultList = new ArrayList<Trade>();
		for(Trade trade:tradeList)
			resultList.add(manager.executeTrade(trade));
		
		return resultList;
	}
	

	@Override
	protected void openPorts() {

		 inportArray = openInputArray("IN");
		 
		 marketManagerPort = openInput("MARKETMANAGER");

		 outport = openOutput("OUT");
	}
}
