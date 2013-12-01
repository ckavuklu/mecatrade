package com.meca.trade.components;

import java.util.ArrayList;
import java.util.List;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.meca.trade.to.IPositionManager;
import com.meca.trade.to.ITestTradeDataSet;
import com.meca.trade.to.ITrader;
import com.meca.trade.to.Order;
import com.meca.trade.to.StrategyDecision;
import com.meca.trade.to.Trade;

/** Sort a stream of Packets to an output stream **/
@ComponentDescription("Messages")
@OutPort(value = "OUT", description = "Output port", type = Order.class)

@InPorts({
	@InPort(value = "MANAGER", description = "position manager interface", type = IPositionManager.class),
	@InPort(value = "TRADER", description = "trader interface", type = ITrader.class),
	@InPort(value = "TESTTRADEDATASET", description = "test trade interface", type = ITestTradeDataSet.class),
	@InPort(value = "IN", arrayPort = true)})



public class PortolioManager extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	InputPort managerPort;
	
	InputPort testTradePort;
	
	InputPort traderPort;
	
	InputPort[] inportArray;
	
	Packet pArray[];
	
	OutputPort outport;
	
	IPositionManager manager = null;
	
	ITrader trader = null;
	
	ITestTradeDataSet testTradeDataSet = null;

	List<Trade> tradeList = new ArrayList<Trade>();
	
	boolean endOfMarketData = false;
	
	@Override
	protected void execute() {

		int no = inportArray.length;
	    pArray = new Packet[no];
	    
	    List<StrategyDecision> strategyDecisions = new ArrayList<StrategyDecision>();
	    
	    
	    
	    Packet managerPer = null;
	    Packet tradeT = null;
	    Packet tradePer = null;
	    
	    if(manager == null){
	    	managerPer = managerPort.receive();
			
			manager = (IPositionManager) managerPer.getContent();
			
			drop(managerPer);
			managerPort.close();
			
			
	    }
	    
	    if(trader == null){
	    	tradeT = traderPort.receive();
			
	    	trader = (ITrader) tradeT.getContent();
			
			drop(tradeT);
			traderPort.close();
			
			
	    }
	    
	    
	    
	    
	    if(testTradeDataSet == null){
	    	tradePer = testTradePort.receive();
			
	    	testTradeDataSet = (ITestTradeDataSet) tradePer.getContent();
			
			drop(tradePer);
			testTradePort.close();
			
			
	    }
	  
	    
	    
	    
	    
	    while((pArray[0] = inportArray[0].receive()) != null){
	    	
	    	
	    	
		    for (int i = 1; i < no; i++) {
		    	pArray[i] = inportArray[i].receive();
		    }
		    
		    

		    
		    
		    for (int i = 0; i < no; i++) {
		    
	    	if (pArray[i] != null) {
	    		  StrategyDecision value = (StrategyDecision) pArray[i].getContent();
	    		  
	    		  if(value.getPrice().getClose() < 0 || value.getPrice().getOpen() < 0
	    				  || value.getPrice().getHigh() < 0 || value.getPrice().getLow() < 0){
	    			  endOfMarketData = true;
	    		  }else{
	    			  manager.updatePriceData(value.getPrice());
	    			  trader.updatePriceData(value.getPrice());
	    			  
	    			  //TODO: We assumed that margin level is 100% of equity for margin call
	    			  if(manager.getFreeMargin()  <= 0d)
	    				  endOfMarketData = true;
	    		  }
	    		  strategyDecisions.add(value);
		    	  
		    	  drop(pArray[i]);
	          }
		    }
		    
		    
		    Order order = null;
		    
		    if(!endOfMarketData){
		    	order = new Order(executeTrades(evaluateStrategyDecisions(strategyDecisions)));
		    }else{
		    	order = new Order(executeTrades(trader.endOfMarket()));
		    }
		    
	    	strategyDecisions.clear();
	    	
		    Packet p = create(order);
			outport.send(p);

	    }
	    
	    
		
	    System.out.println("END OF TRADES");
	    manager.generatePerformanceReport();
	    System.out.println("POSITIONS:");
	    System.out.println(manager);

	}
	
	
	private List<Trade> evaluateStrategyDecisions(final List<StrategyDecision> decisionList){
		List<Trade> result;
		
		result = trader.evaluateStrategyDecisions(decisionList);
		
		for(Trade tr:result){
			System.out.println("Trades:" + tr);
		}
		
		return result;
	}
	
	private List<Trade> evaluateTestDecisions(final List<StrategyDecision> decisionList){
		Trade trade = null;
		
		trade = testTradeDataSet.getNext();
		
		List<Trade> tradeList = new ArrayList<Trade>();
		
		if(trade != null)
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
		 
		 managerPort = openInput("MANAGER");
		 
		 testTradePort = openInput("TESTTRADEDATASET");
		 
		 traderPort = openInput("TRADER");

		 outport = openOutput("OUT");
	}
}
