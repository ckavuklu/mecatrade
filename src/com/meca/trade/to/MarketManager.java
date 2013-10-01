package com.meca.trade.to;

import java.util.List;

public class MarketManager extends MecaObject implements IMarketManager{

	private IPositionManager positionManager;

	private IAccountManager accountManager;
	
	private MarketType type;

	public MarketManager(IPositionManager positionManager,
			IAccountManager accountManager, MarketType type) {
		super();
		this.positionManager = positionManager;
		this.accountManager = accountManager;
		this.type = type;
	}
	
	public Trade executeTrade(Trade trade){
		
		System.out.println("MarketManager.executeTrade() : " + trade.toString());
		
		CurrencyType currency = null;
		Double amount = trade.getLot() * type.getLotSize() * trade.getPrice();
		
		
		switch(trade.getTradeType()){
	    	case Buy : {
	    		currency = type.getQuoteCurrency();
	    		Double balance = accountManager.getBalance(currency);
	    		
	    		if(balance >= amount){
	    			accountManager.withdraw(currency, amount);
	    		}
	    		
	    		break;
	    	}
	    	
	    	case Sell : {
	    		currency = type.getBaseCurrency();
	    		break;
	    	}
	    	default:{
	    		break;
	    	}
		}
		
		
		
		
		
		return trade;
	}
	
    public Trade realizeTrade(Trade trade){
		
    	System.out.println("MarketManager.realizeTrade() : " + trade.toString());
		
    	return trade;
	}
    
    public Trade cancelTrade(Trade trade){
		
    	System.out.println("MarketManager.cancelTrade() : " + trade.toString());
		
    	return trade;
	}

}
