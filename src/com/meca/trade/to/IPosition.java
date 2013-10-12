package com.meca.trade.to;

public interface IPosition {

	public Double getCurrentRisk();
	
	public Double getCurrentProfitLoss(Double quote);
	
	public Double getRealizedProfitLoss();
	
	public Double getRealizedGrossProfit();
	
	public Double getRealizedGrossLoss();
	
	public Integer getTotalNumberOfTrades();
	
	public Integer getTotalNumberOfWinningTrades();
	
	public Integer getTotalNumberOfLosingTrades();
	
	public Integer getTotalNumberOfEntryTrades();
	
	public Double getLargestWinningTrade();
	
	public Double getLargestLosingTrade();
	
	public Trade addTrade(Trade trade);
	
	public String toString();
	
	public TradeStatusType getStatus();
}
