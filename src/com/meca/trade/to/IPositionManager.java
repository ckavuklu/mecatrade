package com.meca.trade.to;

import java.util.List;

public interface IPositionManager extends IPriceData{
	
	public Trade addTrade(Trade data);
	
	public List<IPosition> getPositions();
	
	public Double getOpenPL();
	
	public Integer getConsecutiveWinningTrades();
	
	public Integer getConsecutiveLosingTrades();
	
	public String toString();
	
	public Trade executeTrade(Trade trade);
	public Trade realizeTrade(Trade trade);
	public Trade cancelTrade(Trade trade);
	
	public MarketType getMarketType();
	public void generatePerformanceReport();
	
	
	
	public Double getFreeMargin();
	
}
