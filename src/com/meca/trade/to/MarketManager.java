package com.meca.trade.to;


public class MarketManager extends MecaObject implements IMarketManager{


	private IPositionManager positionManager;

	private IAccountManager accountManager;
	
	private MarketType marketType;
	
	private IPerformanceReportManager perfReporManager;

	public MarketManager(IPositionManager positionManager,
			IAccountManager accountManager, IPerformanceReportManager perfReporManager, MarketType type) {
		super();
		this.positionManager = positionManager;
		this.accountManager = accountManager;
		this.marketType = type;
		this.perfReporManager = perfReporManager;
	}
	
	@Override
	public IAccountManager getAccountManager() {
		
		return accountManager;
	}


	@Override
	public IPositionManager getPositionManager() {
		return positionManager;
	}

	@Override
	public void generatePerformanceReport() {
		perfReporManager.generatePerformanceReport(positionManager, accountManager, marketType);
	}


	
	@Override
	public MarketType getMarketType() {
		return this.marketType;
	}

	public Trade executeTrade(Trade trade){
		
		
		switch(trade.getTradeType()){
	    	case BUY : {
	    		
	    		accountManager.withdraw(positionManager.addTrade(trade));
	    		
	    		break;
	    	}

	    	case LEXIT : {
	    		
	    		positionManager.addTrade(trade);

	    		break;
	    	}

	    	
	    	case SELL : {
	    		
	    		accountManager.withdraw(positionManager.addTrade(trade));
	    		
	    		break;
	    	}

	    	case SEXIT : {
	    		
	    		positionManager.addTrade(trade);
	    		
	    		break;
	    	}

	    	default:{
	    		break;
	    	}
		}
		
		//System.out.println("PositionManager:\r\n" + positionManager.toString());
		//System.out.println("AccountManager:\r\n" + accountManager.toString());
		
		return trade;
	}
	
    public Trade realizeTrade(Trade trade){
		
    	switch(trade.getTradeType()){
	    	case BUY : {
	    		
	    		accountManager.withdraw(positionManager.addTrade(trade));
	
	    		break;
	    	}
	    	
	    	case LEXIT : {
	    		
	    		accountManager.deposit(positionManager.addTrade(trade));

	    		break;
	    	}

	    	case SELL : {
	    		
	    		accountManager.withdraw(positionManager.addTrade(trade));
	    		
	    		break;
	    	}

	    	case SEXIT : {
	    		
	    		accountManager.deposit(positionManager.addTrade(trade));
	    		
	    		break;
	    	}

	    	default:{
	    		break;
	    	}
		}
		
    	System.out.println("PositionManager:\r\n" + positionManager.toString());
		System.out.println("AccountManager:\r\n" + accountManager.toString());
    	
    	return trade;
	}
    
    
    
    public Trade cancelTrade(Trade trade){
		
    	System.out.println("MarketManager.cancelTrade()");
		accountManager.cancel(trade);
		positionManager.addTrade(trade);
		
		//System.out.println("PositionManager:\r\n" + positionManager.toString());
		//System.out.println("AccountManager:\r\n" + accountManager.toString());
	    	
    	return trade;
	}

}
