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
	
	public void executeTrade(){
		
		//TODO:Fill in Here
		
	}
	
    public void realizeTrade(){
		
	}

}
