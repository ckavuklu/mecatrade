package com.meca.trade.to;

public interface IPosition {

	public Double getCurrentRisk();
	
	public Double getCurrentProfitLoss();
	
	public Trade addTrade(Trade trade);
}
