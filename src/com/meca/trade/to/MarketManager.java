package com.meca.trade.to;

import java.util.List;

public class MarketManager extends MecaObject implements IMarketManager{

	private IPositionManager positionManager;

	private IAccountManager accountManager;

	public MarketManager(IPositionManager positionManager,
			IAccountManager accountManager) {
		super();
		this.positionManager = positionManager;
		this.accountManager = accountManager;
	}

}
